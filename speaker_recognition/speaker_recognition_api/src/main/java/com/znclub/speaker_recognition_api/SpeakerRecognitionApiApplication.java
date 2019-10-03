package com.znclub.speaker_recognition_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class SpeakerRecognitionApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeakerRecognitionApiApplication.class, args);
	}

}
