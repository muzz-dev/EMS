package com.example.demo.service;

public interface EmailService {
    public void sendForgotPasswordEmail(String to, String subject, String requestPath,
                                        String resetPasswordLink) throws Exception;
}
