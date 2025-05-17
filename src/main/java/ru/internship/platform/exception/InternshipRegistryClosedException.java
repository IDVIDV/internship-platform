package ru.internship.platform.exception;

public class InternshipRegistryClosedException extends RuntimeException {
    public InternshipRegistryClosedException(String message) {
        super(message);
    }
}
