package com.znclub.speaker_recognition_api.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Component
public class LocalFileUtilityImpl implements FileUtility {

    @Override
    public File fetchByPath(String path) throws IOException {

        String resourcePath = Optional.of(path)
                .orElseThrow( () -> new IOException("File cannot be located due to empty path"));

        InputStream resourceAsStream = FileUtility.class.getResourceAsStream(resourcePath);
        return create(resourceAsStream);
    }
}
