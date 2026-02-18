package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service("fileService")
public class FileService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    Random random = new Random();

    public String processFile(String fileName) throws IOException {
        log.info("Processing File :{}", fileName);
        //simulate RANDOM IO EXception
        if (random.nextInt(3) == 0) {
            throw new IOException("Failed to read file:" + fileName);
        }
        log.info("Processing File Completed:{}", fileName);
        return "File Content from " + fileName;

    }

    public void saveToFile(String content) {
        log.info("Saving Content to File :{}", content);

    }

}
