package org.example.gongiklifeclientbemailservice.service;

import dto.mail.EmailVerificationRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbemailservice.exception.SendEmailException;
import org.example.gongiklifeclientbemailservice.provider.EmailTemplateProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private static final String EMAIL_CHARSET = "UTF-8";
  private static final String VERIFICATION_SUBJECT = "Email Verification Code";
  private final EmailTemplateProvider emailTemplateProvider;
  private final JavaMailSender mailSender;

  public void sendEmail(EmailVerificationRequestDto request) {
    try {
      MimeMessage message = createEmailMessage(request);
      mailSender.send(message);
      log.info("Verification email sent successfully to: {}", request.getEmail());
    } catch (RuntimeException | MessagingException e) {
      log.error("Failed to send verification email to: {}", request.getEmail(), e);
      throw new SendEmailException("Failed to send verification email", e);
    }
  }

  private MimeMessage createEmailMessage(EmailVerificationRequestDto request)
      throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_CHARSET);

    helper.setTo(request.getEmail());
    helper.setSubject(VERIFICATION_SUBJECT);
    helper.setText(emailTemplateProvider.getVerificationEmailTemplate(request.getCode()), true);

    return message;
  }
}

