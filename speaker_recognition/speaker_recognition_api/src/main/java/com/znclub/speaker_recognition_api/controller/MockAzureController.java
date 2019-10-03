package com.znclub.speaker_recognition_api.controller;

import com.znclub.speaker_recognition_api.model.SpeakerProfile;
import com.znclub.speaker_recognition_api.service.SupervisedSpeakerRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("mock/azure")
@Profile("mock")
public class MockAzureController {

    private SupervisedSpeakerRecognitionService recognitionService;

    @Autowired
    public MockAzureController(SupervisedSpeakerRecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    @PostMapping("/identificationProfiles")
    public Map<String, String> createProfile(@RequestBody Map<String, String> request){
        String randomProfileId = recognitionService.findAllSpeakerProfiles()
                .stream()
                .findAny()
                .map(SpeakerProfile::getProfileId)
                .orElse("000");
        return new HashMap(){{put("identificationProfileId", randomProfileId);}};
    }
}
