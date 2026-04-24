package com.viajes.viajes.controller;

import com.viajes.viajes.repository.BitacoraRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Sort;

@Controller
public class ViajeController {

    private final BitacoraRepository bitacoraRepository;
    private final com.viajes.viajes.repository.SponsorRepository sponsorRepository;

    public ViajeController(BitacoraRepository bitacoraRepository, com.viajes.viajes.repository.SponsorRepository sponsorRepository) {
        this.bitacoraRepository = bitacoraRepository;
        this.sponsorRepository = sponsorRepository;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("bitacoras", bitacoraRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")));
        model.addAttribute("sponsors", sponsorRepository.findByActivoTrue());
        return "viaje"; // busca index.html en templates
    }
}