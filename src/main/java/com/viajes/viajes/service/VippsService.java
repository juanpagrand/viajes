package com.viajes.viajes.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;

@Service
public class VippsService {

    @Value("${vipps.client-id}")
    private String clientId;

    @Value("${vipps.client-secret}")
    private String clientSecret;

    @Value("${vipps.subscription-key}")
    private String subscriptionKey;

    @Value("${vipps.msn}")
    private String msn;

    /**
     * Simula la creación de una sesión de pago eCom con Vipps.
     * En una implementación real, esto haría:
     * 1. POST https://api.vipps.no/accessToken/get
     * 2. POST https://api.vipps.no/ecomm/v2/payments
     */
    public String createPaymentSession(double amount, String orderId, String returnUrl) {
        // En un caso real:
        // HttpHeaders headers = new HttpHeaders();
        // headers.set("client_id", clientId);
        // headers.set("client_secret", clientSecret);
        // headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
        // headers.set("Merchant-Serial-Number", msn);
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
