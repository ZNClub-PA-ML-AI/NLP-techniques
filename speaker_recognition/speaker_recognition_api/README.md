# Getting Started

## Default Speaker Recognition API

## AZURE

### Profile id
- 396e15db-c58a-4c32-95b3-d74e51ed94b9 at 2019-06-17 09:51
- bfa6b133-242e-4629-88e0-911420db00dd at 2019-06-17 23:57

## Enrollment status=Enrolling
https://westus.api.cognitive.microsoft.com/spid/v1.0/operations/77bfb77f-a4db-4395-9aeb-14c50baae8a3
https://westus.api.cognitive.microsoft.com/spid/v1.0/operations/e721b88d-b2a4-43dc-b87d-647e11ca3f36

## Successful recognition status

- converter4.wav for enrollment
- converter5.wav for enrollment

{
  "status": "succeeded",
  "createdDateTime": "2019-06-19T17:05:34.6032531Z",
  "lastActionDateTime": "2019-06-19T17:05:37.715093Z",
  "processingResult": {
    "identifiedProfileId": "396e15db-c58a-4c32-95b3-d74e51ed94b9",
    "confidence": "High"
  }
}

## API with Mock Azure Service
- VM args: -Dspring.profiles.active=default,mock
- Uncomment @Profile("mock") in AzureApi.java


## Links

- [record audio](https://www.speakpipe.com/voice-recorder)
- [convert audio](https://audio.online-convert.com/convert-to-wav)