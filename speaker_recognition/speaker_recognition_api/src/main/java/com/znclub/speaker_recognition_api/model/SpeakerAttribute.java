package com.znclub.speaker_recognition_api.model;

import lombok.Data;

import java.io.File;

@Data
public class SpeakerAttribute {
    private SpeakerProfile speakerProfile;
    private File speech;
    private int lengthOfSpeech;
    private Resource resource;
}
