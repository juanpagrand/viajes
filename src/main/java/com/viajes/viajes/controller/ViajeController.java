package com.viajes.viajes.controller;

import com.viajes.viajes.repository.BitacoraRepository;
import com.viajes.viajes.repository.DestinoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Sort;

@Controller
public class ViajeController {

    private final BitacoraRepository bitacoraRepository;
    private final com.viajes.viajes.repository.SponsorRepository sponsorRepository;
    private final DestinoRepository destinoRepository;

    public ViajeController(BitacoraRepository bitacoraRepository, com.viajes.viajes.repository.SponsorRepository sponsorRepository, DestinoRepository destinoRepository) {
        this.bitacoraRepository = bitacoraRepository;
        this.sponsorRepository = sponsorRepository;
        this.destinoRepository = destinoRepository;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("bitacoras", bitacoraRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")));
        model.addAttribute("sponsors", sponsorRepository.findByActivoTrue());
        model.addAttribute("destinos", destinoRepository.findByActivoTrue());
        return "viaje"; // busca index.html en templates
    }
}