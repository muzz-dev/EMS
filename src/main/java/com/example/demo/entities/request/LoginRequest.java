package com.example.demo.entities.request;

import javax.validation.constraints.NotBlank;

public class LoginRequest {

    private String username;

    private String jwtPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJwtPassword() {
        return jwtPassword;
    }

    public void setJwtPassword(String jwtPassword) {
        this.jwtPassword = jwtPassword;
    }
}
