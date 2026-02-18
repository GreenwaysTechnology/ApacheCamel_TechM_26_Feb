package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service("databaseService")
public class DatabaseService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseService.class);
    private Random random = new Random();

    public void saveToDatabase(String data) throws DatabaseException {
        log.info("Saving to database: {}", data);

        // Simulate random database exception
        if (random.nextInt(3) == 0) {
            throw new DatabaseException("Database connection failed");
        }

        log.info("Data saved to database successfully");
    }

    public void cleanup() {
        log.info("Database cleanup performed");
    }
}
