package com.viajes.viajes.controller;

import com.viajes.viajes.model.Donacion;
import com.viajes.viajes.repository.DonacionRepository;
import com.viajes.viajes.service.VippsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DonacionController {

    private final VippsService vippsService;
    private final DonacionRepository donacionRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public DonacionController(VippsService vippsService, DonacionRepository donacionRepository) {
        this.vippsService = vippsService;
        this.donacionRepository = donacionRepository;
    }

    @GetMapping("/donar")
    public String showDonationPage() {
        return "donar";
    }

    @PostMapping("/donar/procesar")
    public String processDonation(@RequestParam("amount") double amount, Model model) {
        if (amount < 1 || amount > 100000) {
            model.addAttribute("error", "Monto no válido");
            return "donar";
        }

        String orderId = vippsService.generateOrderId();

        // Guardar como PENDIENTE — se confirma solo cuando Vipps llama el webhook
        Donacion donacion = new Donacion();
        donacion.setOrderId(orderId);
        donacion.setMonto(amount);
        donacion.setEstado("PENDIENTE");
        donacionRepository.save(donacion);

        String returnUrl = baseUrl + "/donar/exito?orderId=" + orderId;
        String paymentUrl = vippsService.createPaymentSession(amount, orderId, returnUrl);

        return "redirect:" + paymentUrl;
    }

    // Solo muestra la pantalla de confirmación — la lógica real está en el webhook
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