package com.znclub.speaker_recognition_api.service;

import com.znclub.speaker_recognition_api.model.RecognitionStatus;
import com.znclub.speaker_recognition_api.model.SpeakerAttribute;
import com.znclub.speaker_recognition_api.model.SpeakerProfile;

import java.util.Map;

public interface SupervisedSpeakerRecognitionService extends SpeakerRecognitionService {

    RecognitionStatus addSpeaker(SpeakerAttribute speakerAttribute) throws InterruptedException;

    SpeakerProfile createSpeaker(Map<String, String> attributes);
}
