package ru.internship.platform.exception;

public class ForkFailedException extends RuntimeException {
    public ForkFailedException(String message) {
        super(message);
    }
}
