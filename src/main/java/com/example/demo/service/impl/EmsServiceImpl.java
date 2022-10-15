package com.example.demo.service.impl;

import com.example.demo.dao.ForgotPasswordRepository;
import com.example.demo.dao.UserMasterRepository;
import com.example.demo.entities.ForgotPassword;
import com.example.demo.entities.UserMaster;
import com.example.demo.service.EmailService;
import com.example.demo.service.EmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
@Service
public class EmsServiceImpl implements EmsService {

    @Autowired
    private UserMasterRepository userRepository;
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public void processForgotPassword(String emailId, String requestURL) throws Exception {
        Optional<UserMaster> user = userRepository.findById(emailId);
        Optional<ForgotPassword> isUserExist = forgotPasswordRepository.findByUserName(emailId);

        if (!user.isPresent()) {
            throw new IllegalArgumentException("Email Address is not found!");
        }
        String token = UUID.randomUUID().toString();

        if(isUserExist.isPresent())
        {
            isUserExist.get().setRequestTime(new Date());
            isUserExist.get().setToken(token);
            forgotPasswordRepository.save(isUserExist.get());
        }
        else {
            ForgotPassword forgotPassword = new ForgotPassword();
            forgotPassword.setUserMaster(userRepository.findById(emailId).get());
            forgotPassword.setRequestTime(new Date());
            forgotPassword.setToken(token);
            forgotPasswordRepository.save(forgotPassword);
        }

        String passwordResetLink = requestURL + "/resetPassword?token=" + token;
        emailService.sendForgotPasswordEmail(emailId, "DEMO : Password Reset", requestURL, passwordResetLink);
    }
}
