package com.example;

import java.io.IOException;

public class FileProcessingException extends IOException {
    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
