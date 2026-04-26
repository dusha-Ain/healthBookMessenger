package com.rent.healthbookmessenger.web.kafka.dto;

import com.rent.healthbookmessenger.profile.dto.Profile;

public class UserValidationResponse {
    private String idRequest;
    private boolean exists;
    private Profile profile;
    private String errorMessage;

    public UserValidationResponse() {}

    public String getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
