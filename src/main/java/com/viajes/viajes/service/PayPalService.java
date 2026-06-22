package com.viajes.viajes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class PayPalService {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getApiBaseUrl() {
        return "live".equalsIgnoreCase(mode) ? "https://api-m.paypal.com" : "https://api-m.sandbox.paypal.com";
    }

    public boolean isDemo() {
        return "demo-client-id".equals(clientId);
    }

    public Map<String, String> createPaymentSession(double amount, String orderId, String returnUrl, String cancelUrl) {
        if (isDemo()) {
            String mockPaypalId = "MOCK-PAYPAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String redirectUrl = "/paypal/checkout/simulado?orderId=" + orderId + "&amount=" + amount + "&paypalOrderId=" + mockPaypalId;
            return Map.of("redirectUrl", redirectUrl, "paypalOrderId", mockPaypalId);
        }

        try {
            String accessToken = getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            Map<String, Object> amountMap = Map.of(
                "currency_code", "USD",
                "value", String.format(Locale.US, "%.2f", amount)
            );

            Map<String, Object> purchaseUnit = Map.of(
                "reference_id", orderId,
                "amount", amountMap,
                "description", "Donación a El Loco David"
            );

            Map<String, Object> applicationContext = Map.of(
                "return_url", returnUrl,
                "cancel_url", cancelUrl,
                "user_action", "PAY_NOW",
                "shipping_preference", "NO_SHIPPING"
            );

            Map<String, Object> body = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(purchaseUnit),
                "application_context", applicationContext
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                getApiBaseUrl() + "/v2/checkout/orders",
                entity,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map responseBody = response.getBody();
                String paypalOrderId = (String) responseBody.get("id");
                List<Map> links = (List<Map>) responseBody.get("links");
                String approveUrl = null;

                if (links != null) {
                    for (Map link : links) {
                        if ("approve".equals(link.get("rel"))) {
                            approveUrl = (String) link.get("href");
                            break;
                        }
                    }
                }

                if (approveUrl != null && paypalOrderId != null) {
                    return Map.of("redirectUrl", approveUrl, "paypalOrderId", paypalOrderId);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la orden de PayPal: " + e.getMessage(), e);
        }

        throw new RuntimeException("Respuesta inesperada al crear orden de PayPal");
    }

    public boolean capturePayment(String paypalOrderId) {
        if (isDemo()) {
            return true;
        }

        try {
            String accessToken = getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of(), headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                getApiBaseUrl() + "/v2/checkout/orders/" + paypalOrderId + "/capture",
                entity,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String status = (String) response.getBody().get("status");
                return "COMPLETED".equals(status);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al capturar el pago de PayPal: " + e.getMessage(), e);
        }

        return false;
    }

    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
            getApiBaseUrl() + "/v1/oauth2/token",
            entity,
            Map.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }

        throw new RuntimeException("No se pudo obtener el token de acceso de PayPal");
    }

    public String generateOrderId() {
        return "EL-LOCO-PAYPAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
