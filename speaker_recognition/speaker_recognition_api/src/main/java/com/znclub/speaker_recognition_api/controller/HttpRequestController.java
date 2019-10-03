package com.znclub.speaker_recognition_api.controller;

import com.znclub.speaker_recognition_api.model.RecognitionRequest;
import com.znclub.speaker_recognition_api.model.RecognitionStatus;
import com.znclub.speaker_recognition_api.model.SpeakerProfile;
import com.znclub.speaker_recognition_api.service.SupervisedSpeakerRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/speaker-recognition/v1/supervised/azure")
public class HttpRequestController {

    private SupervisedSpeakerRecognitionService recognitionService;

    @Autowired
    public HttpRequestController(SupervisedSpeakerRecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    @GetMapping("speaker-profiles")
    public List<SpeakerProfile> findAllSpeakerProfiles() {
        return recognitionService.findAllSpeakerProfiles();
    }

    @PostMapping("/add-speaker/filesystem")
    public RecognitionStatus addSpeaker(@RequestBody RecognitionRequest request) throws InterruptedException {

        return recognitionService.addSpeaker(request.getSpeakerAttribute());
    }

    @PostMapping("/identify-speaker/filesystem")
    public RecognitionStatus identifySpeaker(@RequestBody RecognitionRequest request) throws InterruptedException {

        return recognitionService.identifySpeaker(request.getSpeakerAttribute(),
                recognitionService.findAllSpeakerProfiles());
    }


}
