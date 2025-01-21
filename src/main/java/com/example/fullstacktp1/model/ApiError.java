package com.example.fullstacktp1.model;

import java.util.Map;

public class ApiError {
    private int status;
    private String message;
    private long timestamp;
    private Map<String, String> validationErrors; // Optionnel, utilisé pour les erreurs de validation

    // Constructeur pour les erreurs sans validation spécifique
    public ApiError(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Constructeur pour les erreurs avec validation
    public ApiError(int status, String message, long timestamp, Map<String, String> validationErrors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.validationErrors = validationErrors;
    }

    // Getters et setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
