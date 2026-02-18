package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service("databaseService")
public class DatabaseService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    Random random = new Random();

    public void saveToDatabase(String data) throws DatabaseException {
        log.info("Saving to  Database :{}", data);
        //simulate RANDOM database EXception
        if (random.nextInt(3) == 0) {
            throw new DatabaseException("Database Connection Failed");
        }
        log.info("Data saved to database");

    }

    public void cleanUp(String content) {
        log.info("Database cleanUp Performed");

    }

}
