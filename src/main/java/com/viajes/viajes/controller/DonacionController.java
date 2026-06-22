package com.viajes.viajes.controller;

import com.viajes.viajes.model.Donacion;
import com.viajes.viajes.repository.DonacionRepository;
import com.viajes.viajes.service.PayPalService;
import com.viajes.viajes.service.VippsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DonacionController {

    private final VippsService vippsService;
    private final PayPalService paypalService;
    private final DonacionRepository donacionRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public DonacionController(VippsService vippsService, PayPalService paypalService, DonacionRepository donacionRepository) {
        this.vippsService = vippsService;
        this.paypalService = paypalService;
        this.donacionRepository = donacionRepository;
    }

    @GetMapping("/donar")
    public String showDonationPage() {
        return "donar";
    }

    @PostMapping("/donar/procesar")
    public String processDonation(
            @RequestParam("amount") double amount,
            @RequestParam(value = "paymentMethod", defaultValue = "vipps") String paymentMethod,
            Model model) {
        
        if (amount < 1 || amount > 100000) {
            model.addAttribute("error", "Monto no válido");
            return "donar";
        }

        if ("paypal".equalsIgnoreCase(paymentMethod)) {
            String orderId = paypalService.generateOrderId();

            Donacion donacion = new Donacion();
            donacion.setOrderId(orderId);
            donacion.setMonto(amount);
            donacion.setEstado("PENDIENTE");
            donacion.setMetodoPago("PAYPAL");
            donacionRepository.save(donacion);

            String returnUrl = baseUrl + "/donar/paypal/exito?orderId=" + orderId;
            String cancelUrl = baseUrl + "/donar";
            
            try {
                java.util.Map<String, String> session = paypalService.createPaymentSession(amount, orderId, returnUrl, cancelUrl);
                donacion.setPaypalOrderId(session.get("paypalOrderId"));
                donacionRepository.save(donacion);

                return "redirect:" + session.get("redirectUrl");
            } catch (Exception e) {
                donacion.setEstado("FALLIDA");
                donacionRepository.save(donacion);
                model.addAttribute("error", "Error al iniciar el pago con PayPal: " + e.getMessage());
                return "donar";
            }
        } else {
            String orderId = vippsService.generateOrderId();

            Donacion donacion = new Donacion();
            donacion.setOrderId(orderId);
            donacion.setMonto(amount);
            donacion.setEstado("PENDIENTE");
            donacion.setMetodoPago("VIPPS");
            donacionRepository.save(donacion);

            String returnUrl = baseUrl + "/donar/exito?orderId=" + orderId;
            String paymentUrl = vippsService.createPaymentSession(amount, orderId, returnUrl);

            return "redirect:" + paymentUrl;
        }
    }

    @GetMapping("/donar/paypal/exito")
    public String successPaypalDonation(
            @RequestParam("orderId") String orderId,
            @RequestParam(value = "token", required = false) String token,
            Model model) {
        
        donacionRepository.findByOrderId(orderId).ifPresent(donacion -> {
            String paypalId = donacion.getPaypalOrderId();
            if (paypalId == null || paypalId.isEmpty()) {
                paypalId = token;
            }
            
            boolean captureSuccess = paypalService.capturePayment(paypalId);
            donacion.setEstado(captureSuccess ? "COMPLETADA" : "FALLIDA");
            donacionRepository.save(donacion);
        });

        return "redirect:/donar/exito?orderId=" + orderId;
    }

    // Solo muestra la pantalla de confirmación — la lógica real está en el webhook o retorno
    @GetMapping("/donar/exito")
    public String successDonation(@RequestParam("orderId") String orderId, Model model) {
        model.addAttribute("orderId", orderId);
        donacionRepository.findByOrderId(orderId).ifPresent(d ->
            model.addAttribute("estado", d.getEstado())
        );
        return "donar-exito";
    }

    // Vipps llama este endpoint automáticamente cuando el pago cambia de estado
    @PostMapping("/vipps/webhook")
    @ResponseBody
    public ResponseEntity<String> recibirWebhook(
            @RequestBody String body,
            @RequestHeader(value = "X-Vipps-Authorization", defaultValue = "") String firma) {

        if (!vippsService.validarFirmaWebhook(body, firma)) {
            return ResponseEntity.status(401).body("Firma inválida");
        }

        String orderId = extraerOrderId(body);
        if (orderId == null) {
            return ResponseEntity.badRequest().body("orderId no encontrado");
        }

        boolean pagoCorrecto = vippsService.verificarPago(orderId);

        donacionRepository.findByOrderId(orderId).ifPresent(donacion -> {
            donacion.setEstado(pagoCorrecto ? "COMPLETADA" : "FALLIDA");
            donacionRepository.save(donacion);
        });

        return ResponseEntity.ok("OK");
    }

    @GetMapping("/vipps/checkout/simulado")
    public String simulatedVippsCheckout(@RequestParam("orderId") String orderId,
                                         @RequestParam("amount") double amount,
                                         Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "vipps-checkout";
    }

    @GetMapping("/paypal/checkout/simulado")
    public String simulatedPaypalCheckout(@RequestParam("orderId") String orderId,
                                          @RequestParam("amount") double amount,
                                          @RequestParam("paypalOrderId") String paypalOrderId,
                                          Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        model.addAttribute("paypalOrderId", paypalOrderId);
        return "paypal-checkout";
    }

    private String extraerOrderId(String jsonBody) {
        try {
            int idx = jsonBody.indexOf("\"orderId\"");
            if (idx == -1) return null;
            int start = jsonBody.indexOf("\"", idx + 9) + 1;
            int end = jsonBody.indexOf("\"", start);
            return jsonBody.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }
}