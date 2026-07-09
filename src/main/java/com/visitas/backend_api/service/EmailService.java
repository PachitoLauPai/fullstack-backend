package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.EmailRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:noreply@universidad.edu.pe}")
    private String remitente;

    public void enviarEmail(EmailRequestDTO emailRequest) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(emailRequest.getDestinatario());
            mensaje.setSubject(emailRequest.getAsunto());
            mensaje.setText(emailRequest.getMensaje());

            javaMailSender.send(mensaje);
            log.info("Email enviado exitosamente a: {}", emailRequest.getDestinatario());
        } catch (Exception e) {
            log.error("Error al enviar email: ", e);
            throw new RuntimeException("No se pudo enviar el email: " + e.getMessage());
        }
    }
}
