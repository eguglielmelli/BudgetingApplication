package com.eguglielmelli.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}