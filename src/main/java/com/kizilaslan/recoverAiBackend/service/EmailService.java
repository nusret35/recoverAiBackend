package com.kizilaslan.recoverAiBackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;

    private String loadChangePasswordTemplate(String templateName, String orderNo, String orderUrl) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/email/" + templateName);
        String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        template = template.replace("{{ orderNo }}", orderNo);
        template = template.replace("{{ orderUrl }}", orderUrl);

        return template;
    }

    public void sendHtmlMessage(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("Vitaloop<support@vitaloop.app>");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}