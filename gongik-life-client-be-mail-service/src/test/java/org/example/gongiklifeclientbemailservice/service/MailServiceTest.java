package org.example.gongiklifeclientbemailservice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dto.mail.EmailVerificationRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.gongiklifeclientbemailservice.exception.SendEmailException;
import org.example.gongiklifeclientbemailservice.provider.EmailTemplateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @Mock
  private EmailTemplateProvider emailTemplateProvider;

  @Mock
  private MimeMessage mimeMessage;

  @InjectMocks
  private MailService mailService;

  private EmailVerificationRequestDto requestDto;

  @BeforeEach
  void setUp() {
    requestDto = EmailVerificationRequestDto.builder()
        .email("test@example.com")
        .code("123456")
        .build();
  }

  @Nested
  @DisplayName("sendEmail 메소드는")
  class SendEmailMethod {

    @Test
    @DisplayName("정상적인 요청의 경우 이메일을 성공적으로 전송한다")
    void success_sendsEmail() throws MessagingException {
      // given
      when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
      when(emailTemplateProvider.getVerificationEmailTemplate(anyString()))
          .thenReturn("<html>template</html>");

      // when
      assertDoesNotThrow(() -> mailService.sendEmail(requestDto));

      // then
      verify(mailSender).send(any(MimeMessage.class));
      verify(emailTemplateProvider).getVerificationEmailTemplate("123456");
    }

    @Test
    @DisplayName("메일 전송 실패 시 SendEmailException을 던진다")
    void whenMailSenderFails_throwsSendEmailException() {
      // given
      when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
      when(emailTemplateProvider.getVerificationEmailTemplate(anyString()))
          .thenReturn("<html>template</html>");
      doThrow(new RuntimeException("Failed to send"))
          .when(mailSender).send(any(MimeMessage.class));

      // when & then
      SendEmailException exception = assertThrows(
          SendEmailException.class,
          () -> mailService.sendEmail(requestDto)
      );

      assertEquals("Failed to send verification email", exception.getMessage());
    }

    @Test
    @DisplayName("이메일 템플릿 생성에 실패하면 SendEmailException을 던진다")
    void whenTemplateCreationFails_throwsSendEmailException() {
      // given
      when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
      when(emailTemplateProvider.getVerificationEmailTemplate(anyString()))
          .thenThrow(new RuntimeException("Template creation failed"));

      // when & then
      assertThrows(RuntimeException.class, () -> mailService.sendEmail(requestDto));
    }

    @Test
    @DisplayName("요청 DTO가 null인 경우 NullPointerException을 던진다")
    void whenRequestDtoIsNull_throwsNullPointerException() {
      // when & then
      assertThrows(NullPointerException.class, () -> mailService.sendEmail(null));
    }

    @Test
    @DisplayName("이메일 주소가 null인 경우 예외를 던진다")
    void whenEmailIsNull_throwsException() {
      // given
      when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
      EmailVerificationRequestDto invalidRequest = EmailVerificationRequestDto.builder()
          .email(null)
          .code("123456")
          .build();

      // when & then
      assertThrows(IllegalArgumentException.class, () -> mailService.sendEmail(invalidRequest));
    }

    @Test
    @DisplayName("인증 코드가 null인 경우 예외를 던진다")
    void whenCodeIsNull_throwsException() {
      // given
      EmailVerificationRequestDto invalidRequest = EmailVerificationRequestDto.builder()
          .email("test@example.com")
          .code(null)
          .build();

      // when & then
      assertThrows(NullPointerException.class, () -> mailService.sendEmail(invalidRequest));
    }
  }

  @Nested
  @DisplayName("createEmailMessage 메소드는")
  class CreateEmailMessageMethod {

    @Test
    @DisplayName("올바른 이메일 메시지를 생성한다")
    void createsCorrectEmailMessage() throws MessagingException {
      // given
      when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
      when(emailTemplateProvider.getVerificationEmailTemplate("123456"))
          .thenReturn("<html>template</html>");

      // when
      mailService.sendEmail(requestDto);

      // then
      verify(mailSender).createMimeMessage();
      verify(emailTemplateProvider).getVerificationEmailTemplate("123456");
    }

    @Test
    @DisplayName("MimeMessage 생성 실패 시 예외를 던진다")
    void whenMimeMessageCreationFails_throwsException() {
      // given
      when(mailSender.createMimeMessage())
          .thenThrow(new RuntimeException("Failed to create MimeMessage"));

      // when & then
      assertThrows(RuntimeException.class, () -> mailService.sendEmail(requestDto));
    }
  }
}
