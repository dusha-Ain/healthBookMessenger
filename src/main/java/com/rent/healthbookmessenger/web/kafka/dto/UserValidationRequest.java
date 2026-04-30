package com.rent.healthbookmessenger.web.kafka.dto;

import java.util.UUID;

public class UserValidationRequest {
    private String idRequest;
    private UUID idUser;
    private String action;

    public UserValidationRequest() {}

    public String getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public UUID getIdUser() {
        return idUser;
    }

    public void setIdUser(UUID idUser) {
        this.idUser = idUser;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
