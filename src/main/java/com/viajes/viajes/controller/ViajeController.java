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
    private final com.viajes.viajes.service.RutaService rutaService;

    public ViajeController(BitacoraRepository bitacoraRepository, com.viajes.viajes.repository.SponsorRepository sponsorRepository, DestinoRepository destinoRepository, com.viajes.viajes.service.RutaService rutaService) {
        this.bitacoraRepository = bitacoraRepository;
        this.sponsorRepository = sponsorRepository;
        this.destinoRepository = destinoRepository;
        this.rutaService = rutaService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("bitacoras", bitacoraRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")));
        model.addAttribute("sponsors", sponsorRepository.findByActivoTrue());
        model.addAttribute("destinos", destinoRepository.findByActivoTrue());
        
        java.util.Optional<com.viajes.viajes.model.Ruta> rutaActivaOpt = rutaService.getRutaActiva();
        if (rutaActivaOpt.isPresent()) {
            com.viajes.viajes.model.Ruta ruta = rutaActivaOpt.get();
            double progreso = rutaService.calcularProgreso();
            double[] coords = rutaService.calcularCoordenadasActuales(progreso);
            
            model.addAttribute("rutaActiva", ruta);
            model.addAttribute("progresoRuta", progreso);
            model.addAttribute("latRuta", coords[0]);
            model.addAttribute("lngRuta", coords[1]);
            
            model.addAttribute("latOrigen", ruta.getLatitudOrigen());
            model.addAttribute("lngOrigen", ruta.getLongitudOrigen());
            model.addAttribute("latDestino", ruta.getLatitudDestino());
            model.addAttribute("lngDestino", ruta.getLongitudDestino());
        }

        return "viaje"; // busca index.html en templates
    }
}