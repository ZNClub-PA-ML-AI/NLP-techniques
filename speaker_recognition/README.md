# Speaker Recognition




## SpeechToText Using Azure

-	API Key : a074dcbde846490cba66d1e68dabe3bf
 
## SpeechToText Using IBM

- API Key - 5Yo4gNQzGCvFJ6PQ8_60u-QgrR6rB8TfINdLhqEtPDkf
- URL - https://gateway-lon.watsonplatform.net/speech-to-text/api



## System Design

### Synchronous & Persistent

- [database] ----(speech-data)----> [recognition-request-gateway]
- [recognition-request-gateway] ----(transformed/ untransformed speech-data)----> [recognition-service(s)]
- [recognition-request-gateway] <----(speaker-id)---- [recognition-service(s)]
- [database] <----(speaker-id)---- [recognition-request-gateway]

### Distributed & Scalable

 - [Receiver] ----(speech-data)----> [[recognition-request-queue]] 
 - [[recognition-request-queue]] ----(speech-data by listening)----> [recognition-request-gateway]
 - [recognition-request-gateway] ----(transformed/ untransformed speech-data)----> [[recognition-service-queue]]
 - [[recognition-service-queue]] ----(transformed/ untransformed speech-data by listening)----> [recognition-service(s)]
 - [recognition-service(s)] ----(speaker-id)----> [[recognition-response-queue]]



---
## Resources

### Azure
	-	[Azure API Key](https://bitbucket.org/momassistant/speechtotext-using-azure-api/src/master/README.md)
	- [Azure Speaker Recognition API](https://westus.dev.cognitive.microsoft.com/docs/services/563309b6778daf02acc0a508/operations/5645c3271984551c84ec6797)
	- [Azure Speaker Recognition Demo](https://app.pluralsight.com/library/courses/microsoft-azure-cognitive-services-recognition-bing-speech-api)
	
	

---
## Environment

### Install modules to ibmspeech.py

- conda install pyaudio
- pip install --upgrade ibm-watson


