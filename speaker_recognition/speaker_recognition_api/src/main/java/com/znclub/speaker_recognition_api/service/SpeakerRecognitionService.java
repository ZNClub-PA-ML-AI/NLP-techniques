package com.znclub.speaker_recognition_api.service;

import com.znclub.speaker_recognition_api.model.RecognitionStatus;
import com.znclub.speaker_recognition_api.model.SpeakerAttribute;
import com.znclub.speaker_recognition_api.model.SpeakerProfile;

import java.util.Arrays;
import java.util.List;

public interface SpeakerRecognitionService {

    default List<SpeakerProfile> findAllSpeakerProfiles(){
        SpeakerProfile p1 = new SpeakerProfile();
        p1.setProfileId("396e15db-c58a-4c32-95b3-d74e51ed94b9");

        return Arrays.asList(p1);
    }

    RecognitionStatus identifySpeaker(SpeakerAttribute speakerAttribute, List<SpeakerProfile> profiles) throws InterruptedException;

}
