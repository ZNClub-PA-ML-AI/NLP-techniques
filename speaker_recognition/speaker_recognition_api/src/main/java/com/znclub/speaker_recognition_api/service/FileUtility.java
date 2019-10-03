package com.znclub.speaker_recognition_api.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

public interface FileUtility {

    default File create(InputStream inputStream) throws IOException {

        if(inputStream == null) {
            throw new IOException("Input Stream is null");
        }

        //https://www.baeldung.com/convert-input-stream-to-a-file
        String pathname = String.format("src/main/resources/static/%s.wav", System.currentTimeMillis());
        File targetFile = new File(pathname);
        java.nio.file.Files.copy(
                inputStream,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        IOUtils.closeQuietly(inputStream);
        return targetFile;
    }

    File fetchByPath(String path) throws IOException;
}
