package com.viajes.viajes.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class VippsService {

    /**
     * Simula la creación de una sesión de pago eCom con Vipps.
     * En una implementación real, esto haría:
     * 1. POST https://api.vipps.no/accessToken/get
     * 2. POST https://api.vipps.no/ecomm/v2/payments
     */
    public String createPaymentSession(double amount, String orderId, String returnUrl) {
        // En un caso real:
        // HttpHeaders headers = new HttpHeaders();
        // headers.set("client_id", "...");
        // headers.set("client_secret", "...");
        // headers.set("Ocp-Apim-Subscription-Key", "...");
        // RestTemplate restTemplate = new RestTemplate();
        // ... llamada API real que retorna una URL de "Vipps Checkout".

        // Como no tenemos llaves reales, simulamos la URL que retornaría Vipps
        // Haremos que devuelva una ruta de nuestra propia app para simular la pasarela visualmente.
        return "/vipps/checkout/simulado?orderId=" + orderId + "&amount=" + amount;
    }

    public String generateOrderId() {
        return "EL-LOCO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
