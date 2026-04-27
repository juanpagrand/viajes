package com.viajes.viajes.controller;

import com.viajes.viajes.service.VippsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DonacionController {

    private final VippsService vippsService;

    public DonacionController(VippsService vippsService) {
        this.vippsService = vippsService;
    }

    @GetMapping("/donar")
    public String showDonationPage() {
        return "donar";
    }

    @PostMapping("/donar/procesar")
    public String processDonation(@RequestParam("amount") double amount, Model model) {
        String orderId = vippsService.generateOrderId();
        
        // URL a la que Vipps debería redirigir tras un pago exitoso
        String returnUrl = "http://localhost:8080/donar/exito?orderId=" + orderId;
        
        // Simular llamada a la API de Vipps
        String paymentUrl = vippsService.createPaymentSession(amount, orderId, returnUrl);
        
        // En una app real haríamos: return "redirect:" + paymentUrl;
        // Para la demo, redirigimos a la vista simulada de Vipps:
        return "redirect:" + paymentUrl;
    }

    // --- Controladores Simulados para la Vista de Vipps (Demo) ---
    
    @GetMapping("/vipps/checkout/simulado")
    public String simulatedVippsCheckout(@RequestParam("orderId") String orderId, 
                                         @RequestParam("amount") double amount, 
                                         Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "vipps-checkout"; // Vista que imita la app de Vipps
    }
    
    @GetMapping("/donar/exito")
    public String successDonation(@RequestParam("orderId") String orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "donar-exito";
    }
}
