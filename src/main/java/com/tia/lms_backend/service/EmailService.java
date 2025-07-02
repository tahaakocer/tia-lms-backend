package com.tia.lms_backend.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Async
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Mail gönderildi: " + to);
        } catch (Exception e) {
            log.error("Mail gönderilemedi: " + to, e);
            e.printStackTrace();
        }
    }
    @Async
    public void sendUserCredentialsEmail(String to, String tckn, String password) {
        String subject = "Hesap Bilgileriniz";
        String body = """
                Merhaba,

                Sistemimizde hesabınız başarıyla oluşturulmuştur.
                
                Giriş bilgilerinizi aşağıda bulabilirsiniz:
                
                Kullanıcı Adı (TCKN): %s
                Şifre: %s

                Lütfen giriş yaptıktan sonra şifrenizi değiştiriniz.

                İyi çalışmalar dileriz.

                Saygılarımızla,
                PiA Destek Ekibi
                """.formatted(tckn, password);

        sendEmail(to, subject, body);
    }
}
