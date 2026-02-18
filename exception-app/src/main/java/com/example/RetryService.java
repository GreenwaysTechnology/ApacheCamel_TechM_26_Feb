package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("retryService")
public class RetryService {

    private static final Logger log = LoggerFactory.getLogger(RetryService.class);
    private int attemptCount = 0;

    public String processData(String data) throws IOException {
        attemptCount++;

        log.info("Attempt #{}: Processing {}", attemptCount, data);

        // Fail first 2 times, succeed on 3rd attempt
        if (attemptCount < 3) {
            throw new IOException("Failed on attempt #" + attemptCount);
        }

        log.info("Success on attempt #{}", attemptCount);
        attemptCount = 0; // reset
        return "Processed: " + data;
    }
}
