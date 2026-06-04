package com.viajes.viajes.service;

public interface EmailService {
    void sendRegistrationEmail(String toEmail, String name);
    void sendPasswordResetEmail(String toEmail, String code);
}
