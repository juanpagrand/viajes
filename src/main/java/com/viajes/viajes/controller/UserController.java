package com.viajes.viajes.controller;

import com.viajes.viajes.model.User;
import com.viajes.viajes.repository.BitacoraRepository;
import com.viajes.viajes.repository.DestinoRepository;
import com.viajes.viajes.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Sort;

@Controller
public class UserController {

    private final UserService userService;
    private final BitacoraRepository bitacoraRepository;
    private final com.viajes.viajes.service.RutaService rutaService;
    private final DestinoRepository destinoRepository;

    public UserController(UserService userService, BitacoraRepository bitacoraRepository, com.viajes.viajes.service.RutaService rutaService, DestinoRepository destinoRepository) {
        this.userService = userService;
        this.bitacoraRepository = bitacoraRepository;
        this.rutaService = rutaService;
        this.destinoRepository = destinoRepository;
    }

    @GetMapping("/user")
    public String userDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addAttribute("usuario", user);
        model.addAttribute("bitacoras", bitacoraRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")));
        
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

        return "user-dashboard";
    }

    @GetMapping("/user/bitacora")
    public String userBitacoraFeed(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addAttribute("usuario", user);
        model.addAttribute("bitacoras", bitacoraRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")));
        return "user-bitacora";
    }

    @GetMapping("/user/rutas")
    public String userRutasMap(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addAttribute("usuario", user);
        model.addAttribute("destinos", destinoRepository.findByActivoTrue());
        
        java.util.Optional<com.viajes.viajes.model.Ruta> rutaActivaOpt = rutaService.getRutaActiva();
        if (rutaActivaOpt.isPresent()) {
            com.viajes.viajes.model.Ruta ruta = rutaActivaOpt.get();
            double progreso = rutaService.calcularProgreso();
            double[] coords = rutaService.calcularCoordenadasActuales(progreso);
            
            // Calculo de tiempo
            long minutosTranscurridos = java.time.temporal.ChronoUnit.MINUTES.between(ruta.getFechaInicio(), java.time.LocalDateTime.now());
            double horasTranscurridas = minutosTranscurridos / 60.0;
            
            model.addAttribute("rutaActiva", ruta);
            model.addAttribute("progresoRuta", progreso);
            model.addAttribute("latRuta", coords[0]);
            model.addAttribute("lngRuta", coords[1]);
            model.addAttribute("horasTranscurridas", String.format("%.1f", horasTranscurridas));
            model.addAttribute("horasTotales", String.format("%.1f", ruta.getTiempoEstimadoHoras()));
            
            model.addAttribute("latOrigen", ruta.getLatitudOrigen());
            model.addAttribute("lngOrigen", ruta.getLongitudOrigen());
            model.addAttribute("latDestino", ruta.getLatitudDestino());
            model.addAttribute("lngDestino", ruta.getLongitudDestino());
        }

        return "user-rutas";
    }
}
