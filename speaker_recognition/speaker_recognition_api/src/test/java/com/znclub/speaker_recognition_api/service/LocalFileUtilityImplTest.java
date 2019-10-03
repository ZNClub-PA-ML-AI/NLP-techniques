package com.znclub.speaker_recognition_api.service;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isNotNull;

public class LocalFileUtilityImplTest {

    private LocalFileUtilityImpl utility;

    @Before
    public void setUp() throws Exception {
        utility = new LocalFileUtilityImpl();
    }

    @Test(expected = IOException.class)
    public void fetchByWrongPath() throws IOException {
        utility.fetchByPath("src/path");
    }

    @Test
    public void fetchByCorrectPath() throws IOException {

        String profileId = "speaker1Enroll";
        String path = "/static/" + profileId + ".wav";

        File file = utility.fetchByPath(path);

        assertThat(file, notNullValue());
    }
}