package com.viajes.viajes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViajeController {

    @GetMapping("/")
    public String inicio() {
        return "viaje"; // busca index.html en templates
    }
}