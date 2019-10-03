package com.znclub.speaker_recognition_api.service;

import com.znclub.speaker_recognition_api.api.AzureApi;
import com.znclub.speaker_recognition_api.model.RecognitionStatus;
import com.znclub.speaker_recognition_api.model.Resource;
import com.znclub.speaker_recognition_api.model.SpeakerAttribute;
import com.znclub.speaker_recognition_api.model.SpeakerProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.znclub.speaker_recognition_api.model.Resource.FILESYSTEM;

@Slf4j
@Service
public class AzureSpeakerRecognitionServiceImpl implements SupervisedSpeakerRecognitionService {

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private AzureApi azureApi;

    @Override
    public RecognitionStatus addSpeaker(SpeakerAttribute speakerAttribute) throws InterruptedException {
        RecognitionStatus status = new RecognitionStatus();

        boolean isProfileIdCreated = Optional.ofNullable(speakerAttribute)
                .map(SpeakerAttribute::getSpeakerProfile)
                .map(SpeakerProfile::getProfileId)
                .isPresent();

        if (!isProfileIdCreated) {
            createSpeaker(speakerAttribute);
        }

        try {
            transform(speakerAttribute);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("cannot add speaker due to:" + e.getMessage());
        }

        AzureApi.AzureResponse response = null;
        try {
            response = azureApi.createEnrollment(speakerAttribute);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }

        if (isValid(response)) {

            AzureApi.AzureResponse completedOperation = azureApi.waitForOperationToComplete(response.getResponse());

            if (isValid(response)) {
                status.setStatusMessage("Speaker has been added with following ");

            } else {
                status.setStatusMessage("Speaker could not be added");
            }

        } else {
            log.error("Failed to createSpeaker");

            throw new RuntimeException("Failed to createSpeaker. Not attempting retry");
            //RETRY
//            newProfile = createSpeaker(attributes);
        }

        return status;
    }

    private boolean isValid(AzureApi.AzureResponse response) {
        return Optional.ofNullable(response).map(AzureApi.AzureResponse::getStatus).isPresent();
    }

    @Override
    public SpeakerProfile createSpeaker(Map<String, String> attributes) {
        SpeakerProfile newProfile = new SpeakerProfile();

        AzureApi.AzureResponse response = azureApi.createProfile();

        if (isValid(response)) {
            newProfile.setProfileId((String) response.getResponse().get("profileId"));
        } else {
            log.error("Failed to createSpeaker");

            throw new RuntimeException("Failed to createSpeaker. Not attempting retry");
            //RETRY
//            newProfile = createSpeaker(attributes);
        }

        return newProfile;
    }


    private void createSpeaker(SpeakerAttribute speakerAttribute) {
        SpeakerProfile newSpeaker = createSpeaker(Collections.emptyMap());

        //TODO Copy original values from SpeakerProfile if required

        speakerAttribute.setSpeakerProfile(newSpeaker);
    }


    private void transform(SpeakerAttribute speakerAttribute) throws IOException {

        Resource resource = Optional.ofNullable(speakerAttribute.getResource())
                .orElse(FILESYSTEM);

        if (resource == FILESYSTEM) {

            String pathToResource = speakerAttribute.getSpeakerProfile().getProfileId();
            pathToResource = "converter5";
            File file = fileUtility.fetchByPath("/static/" + pathToResource + ".wav");
            speakerAttribute.setSpeech(file);
        }
    }

    @Override
    public RecognitionStatus identifySpeaker(SpeakerAttribute speakerAttribute, List<SpeakerProfile> profiles) throws InterruptedException {

        RecognitionStatus status = new RecognitionStatus();


        if (speakerAttribute.getSpeech() == null) {

            try {
                transform(speakerAttribute);

            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("cannot add speaker due to:" + e.getMessage());
            }
        }

        AzureApi.AzureResponse response;

        try {
            response = azureApi.identifySpeakerProfile(speakerAttribute, profiles);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }

        if (isValid(response)) {

            AzureApi.AzureResponse completedOperation = azureApi.waitForOperationToComplete(response.getResponse());

            if (isValid(response)) {
                status.setStatusMessage("Speaker has been recognized");

            } else {
                status.setStatusMessage("Speaker has not be recognized");
            }

        } else {
            log.error("Failed to createSpeaker");

            throw new RuntimeException("Failed to createSpeaker. Not attempting retry");
            //RETRY
//            newProfile = createSpeaker(attributes);
        }

        return status;
    }
}
