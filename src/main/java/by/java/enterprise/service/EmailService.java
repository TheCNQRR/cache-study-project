package by.java.enterprise.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationCode(String toEmail, String username, int code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Код подтверждения");
        message.setText("""
                Здравствуйте, %s!

                Ваш код подтверждения: %d

                Подтвердите адрес, перейдя по ссылке:
                http://localhost:8080/auth/confirm-email?email=%s&code=%d

                Код действует 15 минут.
                """.formatted(username, code, toEmail, code));
        mailSender.send(message);
    }
}
