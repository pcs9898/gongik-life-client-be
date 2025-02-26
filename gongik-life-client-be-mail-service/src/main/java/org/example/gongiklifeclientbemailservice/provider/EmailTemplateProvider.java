package org.example.gongiklifeclientbemailservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailTemplateProvider {

  /**
   * 이메일 인증 코드 템플릿을 생성합니다.
   */
  public String getVerificationEmailTemplate(String verificationCode) {
    return """
        <html>
        <body style='font-family: Arial, sans-serif; margin: 0; padding: 0;'>
            <div style='background-color: #f7f7f7; padding: 20px;'>
                <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; 
                          padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>
                    <h1 style='color: #333333;'>Email Verification</h1>
                    <p style='font-size: 16px; color: #666666;'>Your verification code is:</p>
                    <p style='font-size: 24px; font-weight: bold; color: #333333;'>%s</p>
                    <p style='font-size: 14px; color: #999999;'>
                        If you did not request this code, please ignore this email.
                    </p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(verificationCode);
  }
}
