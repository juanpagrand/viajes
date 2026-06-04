package com.viajes.viajes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendRegistrationEmail(String toEmail, String name) {
        System.out.println("=================================================");
        System.out.println("ENVIANDO CORREO DE REGISTRO:");
        System.out.println("Para: " + toEmail);
        System.out.println("Nombre: " + name);
        System.out.println("Mensaje: ¡Bienvenido a bordo de la expedición El Loco David!");
        System.out.println("=================================================");
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("¡Bienvenido a Bordo! - Confirmación de Registro");
            message.setText("Hola " + name + ",\n\nTe damos la bienvenida oficial a la expedición El Loco David. Tu registro ha sido completado con éxito.\n\n¡Buen viaje!");
            mailSender.send(message);
            log.info("Correo de registro enviado con éxito a {}", toEmail);
        } catch (Exception e) {
            log.error("No se pudo enviar el correo de registro a {} por SMTP (se imprimió en consola): {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String code) {
        System.out.println("=================================================");
        System.out.println("ENVIANDO CORREO DE RESTABLECIMIENTO:");
        System.out.println("Para: " + toEmail);
        System.out.println("Código de Confirmación: " + code);
        System.out.println("=================================================");
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Restablecer Contraseña - Código de Confirmación");
            message.setText("Hola,\n\nHas solicitado restablecer tu contraseña. Tu código de confirmación es:\n\n" + code + "\n\nEste código es válido por 15 minutos.\n\nSi no solicitaste este cambio, puedes ignorar este correo.");
            mailSender.send(message);
            log.info("Correo de restablecimiento enviado con éxito a {}", toEmail);
        } catch (Exception e) {
            log.error("No se pudo enviar el correo de restablecimiento a {} por SMTP (se imprimió en consola): {}", toEmail, e.getMessage());
        }
    }
}
