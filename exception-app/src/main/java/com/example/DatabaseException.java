package com.example;

public class DatabaseException extends Exception {
    public DatabaseException() {
        super();
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
