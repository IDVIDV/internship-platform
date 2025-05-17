package ru.internship.platform.exception;

public class UserAlreadyJoinedInternshipException extends RuntimeException {
    public UserAlreadyJoinedInternshipException(String message) {
        super(message);
    }
}
