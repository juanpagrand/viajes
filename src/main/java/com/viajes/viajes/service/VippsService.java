package com.viajes.viajes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Service
public class VippsService {

    @Value("${vipps.client-id}")
    private String clientId;

    @Value("${vipps.client-secret}")
    private String clientSecret;

    @Value("${vipps.subscription-key}")
    private String subscriptionKey;

    @Value("${vipps.merchant-serial-number}")
    private String merchantSerialNumber;

    @Value("${vipps.webhook-secret}")
    private String webhookSecret;

    @Value("${app.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Modo demo automático cuando no hay claves reales configuradas
    private boolean isDemo() {
        return "demo-client-id".equals(clientId);
    }

    public String createPaymentSession(double amount, String orderId, String returnUrl) {
        if (isDemo()) {
            return "/vipps/checkout/simulado?orderId=" + orderId + "&amount=" + amount;
        }

        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
            headers.set("Merchant-Serial-Number", merchantSerialNumber);

            // Vipps requiere el monto en øre (sin decimales)
            int amountInOre = (int) Math.round(amount * 100);

            Map<String, Object> body = Map.of(
                "merchantInfo", Map.of(
                    "merchantSerialNumber", merchantSerialNumber,
                    "callbackPrefix", baseUrl + "/vipps",
                    "fallBack", returnUrl,
                    "isApp", false
                ),
                "customerInfo", Map.of(),
                "transaction", Map.of(
                    "orderId", orderId,
                    "amount", amountInOre,
                    "transactionText", "Donación a El Loco David"
                )
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.vipps.no/ecomm/v2/payments",
                new HttpEntity<>(body, headers),
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("url");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al crear pago en Vipps: " + e.getMessage(), e);
        }

        throw new RuntimeException("Respuesta inesperada de Vipps");
    }

    public boolean verificarPago(String orderId) {
        if (isDemo()) {
            return true;
        }

        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
            headers.set("Merchant-Serial-Number", merchantSerialNumber);

            ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.vipps.no/ecomm/v2/payments/" + orderId + "/details",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map transactionInfo = (Map) response.getBody().get("transactionInfo");
                if (transactionInfo != null) {
                    String status = (String) transactionInfo.get("status");
                    // RESERVE = autorizado, SALE = capturado
                    return "RESERVE".equals(status) || "SALE".equals(status);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar pago: " + e.getMessage(), e);
        }

        return false;
    }

    public boolean validarFirmaWebhook(String body, String signatureHeader) {
        if (isDemo()) {
            return true;
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
            ));
            byte[] hash = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
            String expected = HexFormat.of().formatHex(hash);
            return expected.equals(signatureHeader);
        } catch (Exception e) {
            return false;
        }
    }

    public String generateOrderId() {
        return "EL-LOCO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("client_id", clientId);
        headers.set("client_secret", clientSecret);
        headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://api.vipps.no/accessToken/get",
            new HttpEntity<>(headers),
            Map.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }

        throw new RuntimeException("No se pudo obtener token de Vipps");
    }
}