package com.thoughtworks.jwt.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtResponse {

    @JsonProperty("Token")
    private String token;

    public JwtResponse() {
    }

    public JwtResponse(String token) {
        this.token = token;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
