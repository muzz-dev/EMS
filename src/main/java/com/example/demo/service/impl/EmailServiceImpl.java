package com.example.demo.service.impl;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    public JavaMailSender emailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmailAddress;
    @Override
    public void sendForgotPasswordEmail(String to, String subject, String requestPath, String resetPasswordLink) throws Exception {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        // context.setVariable("userName", userName);
        context.setVariable("oculusHomeUrl", requestPath);
        context.setVariable("resetPasswordLink", resetPasswordLink);

        String html = templateEngine.process("email-template.html", context);

        helper.setTo(to);
        helper.setFrom(fromEmailAddress);
        helper.setText(html, true);
        helper.setSubject(subject);

        emailSender.send(message);
    }
}
