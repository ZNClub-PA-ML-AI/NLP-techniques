package com.znclub.speaker_recognition_api.api;

import com.znclub.speaker_recognition_api.model.SpeakerAttribute;
import com.znclub.speaker_recognition_api.model.SpeakerProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AzureAPi is responsible to handle all communication with Azure Services
 */

@Slf4j
@Component
//@Profile("mock")
public class AzureApi {

    @Value("${api.azure.create-profile}")
    private String createProfileUrl;

    @Value("${api.azure.create-enrollment}")
    private String createEnrollmentUrl;

    @Value("${api.azure.operation-status}")
    private String operationStatusUrl;

    @Value("${api.azure.identification}")
    private String identificationUrl;


    /**
     * Identification Profile - Create Profile
     * https://westus.dev.cognitive.microsoft.com/docs/services/563309b6778daf02acc0a508/operations/5645c068e597ed22ec38f42e
     * <p>
     * Request
     * {
     * "locale":"en-us",
     * }
     * <p>
     * Response 200
     * {
     * "identificationProfileId": "49a36324-fc4b-4387-aa06-090cfbf0064f",
     * }
     * <p>
     * Response 500
     * {
     * "error":{
     * "code" : "InternalServerError",
     * "message" : "SpeakerInvalid",
     * }
     * }
     *
     * @return AzureResponse with final result
     */
    public AzureResponse createProfile() {
        String locale = getConfiguredLocale();

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap() {{
            put("locale", locale);
        }};

        MultiValueMap<String, String> headers = createHeader(MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Map<String, String>> request = new HttpEntity(body, headers);

        ResponseEntity<Map> response = restTemplate
                .exchange(createProfileUrl,
                        HttpMethod.POST,
                        request,
                        Map.class);

        String status = null;

        if (response.getStatusCode() == HttpStatus.OK) {
            body.put("profileId", response.getBody()
                    .get("identificationProfileId"));
            status = "success";
            log.debug("AZUREAPI: identificationProfileId=" + response.getBody()
                    .get("identificationProfileId"));

        } else {
            handleError(response);
            body.put("message", response.getBody()
                    .get("message"));
        }

        return new AzureResponse(status, body);
    }

    private String getConfiguredLocale() {
        return "en-us";
    }


    /**
     * Identification Profile - Create Enrollment
     * https://westus.api.cognitive.microsoft.com/spid/v1.0/identificationProfiles/{identificationProfileId}/enroll[?shortAudio]
     *
     * @param speakerAttribute All attributes of speaker like profileId, Speech
     *                         <p>
     *                         <p>
     *                         <p>
     *                         <p>
     *                         Response 500
     *                         {
     *                         "error":{
     *                         "code" : "InternalServerError",
     *                         "message" : "SpeakerInvalid",
     *                         }
     *                         }
     * @apiNote identificationProfileId: pathParameter profileId as String
     * <p>
     * shortAudio: queryParameter true or false as Boolean.toString()
     * Instruct the service to waive the recommended minimum audio limit needed for enrollment.
     * Set value to “true” to force enrollment using any audio length (min. 1 second).
     */
    public AzureResponse createEnrollment(SpeakerAttribute speakerAttribute) throws IOException {
        File file = speakerAttribute.getSpeech();
        boolean shortAudio = speakerAttribute.getLengthOfSpeech() > 1;
        String identificationProfileId = speakerAttribute.getSpeakerProfile().getProfileId();


//        RestClientException: No HttpMessageConverter for org.springframework.util.LinkedMultiValueMap and content type "audio/wav"
//        MultiValueMap<String, String> headers = createHeader("audio/wav");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        FileEntity fileEntity = new FileEntity(file, "wav/audio");
//        body.add("file", fileEntity);


        HttpClient httpclient = new DefaultHttpClient();
        String URL = String.format(createEnrollmentUrl, identificationProfileId, String.valueOf(shortAudio));
        HttpPost httpPost = new HttpPost(URL);


        MultiValueMap<String, String> headers = createHeader(MediaType.MULTIPART_FORM_DATA_VALUE);
        httpPost.setHeader("Content-Type", headers.getFirst("Content-Type"));
        httpPost.setHeader("Ocp-Apim-Subscription-Key", headers.getFirst("Ocp-Apim-Subscription-Key"));

        // Request body

        FileEntity reqEntity = new FileEntity(file, "wav/audio");
        httpPost.setEntity(reqEntity);
        HttpResponse httpResponse = httpclient.execute(httpPost);

        String status = null;
        Map<String, Object> responseInfo = new HashMap<>();

        if (httpResponse != null) {

            HeaderElement[] elements = httpResponse.getFirstHeader("Operation-Location").getElements();

            String url = Arrays.stream(elements).findFirst().map(HeaderElement::getName).orElse("");

            if (url.isEmpty()) {
                log.error("something went wrong");
            } else {
                responseInfo.put("url", url);
                status = "success";
                log.debug("AZUREAPI: url=" + url);
            }

        } else {
            log.error("something went wrong");
            responseInfo.put("message", Collections.emptyMap()
                    .getOrDefault("message", "Error Message unavailable"));
        }
        return new AzureResponse(status, responseInfo);

    }

    private MultiValueMap<String, String> createHeader(String contentType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", contentType);
        populateWithSecretKey(headers);
        return headers;
    }

    /**
     * @param url Response 200
     * @apiNote status: (notstarted,running,failed,succeeded)
     * <p>
     * EnrollmentStatus: (Enrolling,Training,Enrolled)
     * <p>
     * processingResult: Json Object, present only when operation status=succeeded
     * <p>
     * identifiedProfileId: identified profileId.
     * If this value is 00000000-0000-0000-0000-000000000000,
     * it means there's no speaker identification profile identified
     */
    private AzureResponse fetchOperationStatus(String url) {

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> headers = createHeader(MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Map<String, Object>> request = new HttpEntity(Collections.emptyMap(), headers);

        ResponseEntity<Map> response = restTemplate
                .exchange(url,
                        HttpMethod.GET,
                        request,
                        Map.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            handleError(response);
        }
        return new AzureResponse(response.getBody().get("status").toString(),
                response.getBody());
    }

    private void handleError(ResponseEntity<Map> response) {
        if (log.isErrorEnabled()) {
            log.error("Failed to createProfile due to:" +
                    response.getBody()
                            .getOrDefault("code", "Error Code missing"));
        }
    }

    private void populateWithSecretKey(MultiValueMap<String, String> headers) {
        final String secretKey = "aee88a7709d8416cab8c48f190aa4a69";
        headers.put("Ocp-Apim-Subscription-Key", Collections.singletonList(secretKey));
    }

    public AzureResponse waitForOperationToComplete(Map<String, Object> response) throws InterruptedException {

        AzureResponse completedOperation = new AzureResponse();
        int retryCounter = 10;
        boolean waitForOperationToComplete = true;
        String url = (String) response.get("url");

        while (retryCounter > 0 && waitForOperationToComplete) {
            AzureResponse operationResponse = fetchOperationStatus(url);

            switch (operationResponse.getStatus()) {

                case "succeeded":
                    completedOperation = new AzureResponse("success", operationResponse.getResponse());
                    waitForOperationToComplete = false;
                    break;

                case "failed":
                    throw new RuntimeException("Operation status has failed for url:" + response.get("url"));

                case "running":
                case "notstarted":
                    Thread.sleep(30000);
                    retryCounter--;
            }
        }

        return completedOperation;
    }

    public AzureResponse identifySpeakerProfile(SpeakerAttribute speakerAttribute, List<SpeakerProfile> profiles) throws IOException {

        File file = speakerAttribute.getSpeech();
        boolean shortAudio = speakerAttribute.getLengthOfSpeech() > 1;
        String identificationProfileIds = profiles.stream()
                .map(SpeakerProfile::getProfileId)
                .collect(Collectors.joining());

        HttpClient httpclient = new DefaultHttpClient();
        String URL = String.format(identificationUrl, identificationProfileIds, String.valueOf(shortAudio));
        HttpPost httpPost = new HttpPost(URL);


        MultiValueMap<String, String> headers = createHeader(MediaType.MULTIPART_FORM_DATA_VALUE);
        httpPost.setHeader("Content-Type", headers.getFirst("Content-Type"));
        httpPost.setHeader("Ocp-Apim-Subscription-Key", headers.getFirst("Ocp-Apim-Subscription-Key"));

        // Request body

        FileEntity reqEntity = new FileEntity(file, "wav/audio");
        httpPost.setEntity(reqEntity);
        HttpResponse httpResponse = httpclient.execute(httpPost);

/*      second solution

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

// This attaches the file to the POST:

        builder.addBinaryBody(
                "file",
                new FileInputStream(file),
                ContentType.APPLICATION_OCTET_STREAM,
                file.getName()
        );

        org.apache.http.HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        HttpResponse httpResponse = httpclient.execute(httpPost);
*/

        String status = null;
        Map<String, Object> responseInfo = new HashMap<>();

        if (httpResponse != null) {

            HeaderElement[] elements = httpResponse.getFirstHeader("Operation-Location").getElements();

            String url = Arrays.stream(elements).findFirst().map(HeaderElement::getName).orElse("");

            if (url.isEmpty()) {
                log.error("something went wrong");
            } else {
                responseInfo.put("url", url);
                status = "success";
                log.debug("AZUREAPI: url=" + url);
            }

        } else {
            log.error("something went wrong");
            responseInfo.put("message", Collections.emptyMap()
                    .getOrDefault("message", "Error Message unavailable"));
        }
        return new AzureResponse(status, responseInfo);
    }

    //TODO Try Lombok
//    @AllArgsConstructor
    public class AzureResponse {
        private String status;
        private Map<String, Object> response;

        public AzureResponse(String status,
                             Map<String, Object> response) {
            this.status = status;
            this.response = response;
        }

        public AzureResponse() {

        }

        public String getStatus() {
            return status;
        }

        public Map<String, Object> getResponse() {
            return response;
        }
    }
}
