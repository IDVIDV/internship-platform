package ru.internship.platform.exception;

public class WrongInternshipStatusException extends RuntimeException {
    public WrongInternshipStatusException(String message) {
        super(message);
    }
}
