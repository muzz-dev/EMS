package com.example.demo.entities.response;

import com.example.demo.entities.Deployment;

import java.util.Date;
import java.util.List;

public class JwtResponse {

    private long jwtExpirationMs = 2592000000l;
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;
    private String firstname;
    private String lastname;

    private Deployment deployment;
//    private Date expiryTime = new Date((new Date()).getTime() + jwtExpirationMs);

    public JwtResponse(String accessToken, String username, List<String> roles, String firstname, String lastname,Deployment deployment) {
        this.token = accessToken;
        this.username = username;
        this.roles = roles;
        this.firstname = firstname;
        this.lastname = lastname;
        this.deployment = deployment;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public void setDeployment(Deployment deployment) {
        this.deployment = deployment;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
//
//    public Date getExpiryTime() {
//        return expiryTime;
//    }
//
//    public void setExpiryTime(Date expiryTime) {
//        this.expiryTime = expiryTime;
//    }

}
