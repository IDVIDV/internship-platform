package ru.internship.platform.exception;

public class GitlabServiceException extends RuntimeException {
    public GitlabServiceException(String message) {
        super(message);
    }
}
