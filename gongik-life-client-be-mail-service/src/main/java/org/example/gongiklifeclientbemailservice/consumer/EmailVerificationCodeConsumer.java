package org.example.gongiklifeclientbemailservice.consumer;

import dto.UserToEmail.EmailVerificationRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationCodeConsumer {

  private final JavaMailSender mailSender;


  @KafkaListener(topics = "email-verification-topic")
  public void consume(EmailVerificationRequestDto request) throws MessagingException {

    sendEmail(request);


  }

  private void sendEmail(EmailVerificationRequestDto request) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    helper.setTo(request.getEmail());
    helper.setSubject("Email Verification Code");
    helper.setText(
        "<html>" +
            "<body style='font-family: Arial, sans-serif; margin: 0; padding: 0;'>" +
            "<div style='background-color: #f7f7f7; padding: 20px;'>" +
            "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>"
            +
            "<h1 style='color: #333333;'>Email Verification</h1>" +
            "<p style='font-size: 16px; color: #666666;'>Your verification code is:</p>" +
            "<p style='font-size: 24px; font-weight: bold; color: #333333;'>" + request.getCode()
            + "</p>" +
            "<p style='font-size: 14px; color: #999999;'>If you did not request this code, please ignore this email.</p>"
            +
            "</div>" +
            "</div>" +
            "</body>" +
            "</html>", true);

    mailSender.send(message);
  }


}
