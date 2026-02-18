package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service("fileService")
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private Random random = new Random();

    public String processFile(String fileName) throws IOException {
        log.info("Processing file: {}", fileName);

        // Simulate random IO exception
        if (random.nextInt(3) == 0) {
            throw new IOException("Failed to read file: " + fileName);
        }

        log.info("File processed successfully: {}", fileName);
        return "File content from: " + fileName;
    }

    public void saveToFile(String content) {
        log.info("Saving content to file: {}", content);
    }
}

