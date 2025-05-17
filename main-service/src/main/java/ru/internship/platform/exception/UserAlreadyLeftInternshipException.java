package ru.internship.platform.exception;

public class UserAlreadyLeftInternshipException extends RuntimeException {
    public UserAlreadyLeftInternshipException(String message) {
        super(message);
    }
}
