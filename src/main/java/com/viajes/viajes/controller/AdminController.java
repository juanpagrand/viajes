package com.viajes.viajes.controller;

import com.viajes.viajes.model.User;
import com.viajes.viajes.model.UserDto;
import com.viajes.viajes.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.viajes.viajes.model.Bitacora;
import com.viajes.viajes.repository.BitacoraRepository;
import com.viajes.viajes.service.FileStorageService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.viajes.viajes.model.Destino;
import com.viajes.viajes.repository.DestinoRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final BitacoraRepository bitacoraRepository;
    private final FileStorageService fileStorageService;
    private final com.viajes.viajes.repository.SponsorRepository sponsorRepository;
    private final DestinoRepository destinoRepository;
    private final com.viajes.viajes.service.RutaService rutaService;

    public AdminController(UserService userService, BitacoraRepository bitacoraRepository, FileStorageService fileStorageService, com.viajes.viajes.repository.SponsorRepository sponsorRepository, DestinoRepository destinoRepository, com.viajes.viajes.service.RutaService rutaService) {
        this.userService = userService;
        this.bitacoraRepository = bitacoraRepository;
        this.fileStorageService = fileStorageService;
        this.sponsorRepository = sponsorRepository;
        this.destinoRepository = destinoRepository;
        this.rutaService = rutaService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        return "admin-dashboard";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("admin") UserDto userDto, 
                                @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            String fotoPath = fileStorageService.storeFile(fotoArchivo);
            userDto.setFoto(fotoPath);
        }
        
        userService.updateUserProfile(auth.getName(), userDto);
        return "redirect:/admin?success";
    }

    @GetMapping("/register")
    public String showAdminRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("adminDto", user);
        return "admin-register";
    }

    @PostMapping("/register/save")
    public String registerAdmin(@ModelAttribute("adminDto") UserDto userDto, Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            return "redirect:/admin/register?error";
        }

        userService.saveAdmin(userDto);
        
        // Return to admin dashboard with success
        return "redirect:/admin?success";
    }

    @GetMapping("/bitacora/nueva")
    public String showBitacoraForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        model.addAttribute("bitacora", new Bitacora());
        return "bitacora-form";
    }

    @PostMapping("/bitacora/guardar")
    public String saveBitacora(@ModelAttribute("bitacora") Bitacora bitacora,
                               @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo) {
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            String fotoPath = fileStorageService.storeFile(fotoArchivo);
            bitacora.setFotoAdjunta(fotoPath);
        }
        
        bitacoraRepository.save(bitacora);
        return "redirect:/admin?successBitacora";
    }

    @GetMapping("/tripulacion")
    public String gestionarTripulacion(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        
        List<User> admins = userService.findAllUsers().stream()
                .filter(u -> u.getRole() == com.viajes.viajes.model.Role.ROLE_ADMIN)
                .collect(java.util.stream.Collectors.toList());
                
        model.addAttribute("usuarios", admins);
        return "admin-tripulacion";
    }

    @PostMapping("/tripulacion/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/admin/tripulacion?successStatus";
    }

    @GetMapping("/tripulacion/editar/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        
        User targetUser = userService.findUserById(id);
        if (targetUser == null) {
            return "redirect:/admin/tripulacion?errorNotFound";
        }
        
        UserDto userDto = new UserDto();
        userDto.setNombre(targetUser.getNombre());
        userDto.setEmail(targetUser.getEmail());
        userDto.setDescripcion(targetUser.getDescripcion());
        userDto.setFoto(targetUser.getFoto());
        
        model.addAttribute("targetUserDto", userDto);
        model.addAttribute("targetUserId", id);
        return "admin-editar-usuario";
    }

    @PostMapping("/tripulacion/editar/{id}")
    public String updateUser(@PathVariable Long id, 
                             @ModelAttribute("targetUserDto") UserDto userDto,
                             @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo) {
        
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            String fotoPath = fileStorageService.storeFile(fotoArchivo);
            userDto.setFoto(fotoPath);
        }
        
        userService.updateUserById(id, userDto);
        return "redirect:/admin/tripulacion?successEdit";
    }

    // --- SPONSORS MANAGEMENT ---

    @GetMapping("/sponsors")
    public String gestionarSponsors(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        
        List<com.viajes.viajes.model.Sponsor> sponsors = sponsorRepository.findAll();
        model.addAttribute("sponsors", sponsors);
        model.addAttribute("nuevoSponsor", new com.viajes.viajes.model.Sponsor());
        return "admin-sponsors";
    }

    @PostMapping("/sponsors/guardar")
    public String saveSponsor(@ModelAttribute("nuevoSponsor") com.viajes.viajes.model.Sponsor sponsor,
                              @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo) {
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            String fotoPath = fileStorageService.storeFile(fotoArchivo);
            sponsor.setLogoPath(fotoPath);
        }
        sponsorRepository.save(sponsor);
        return "redirect:/admin/sponsors?successAdd";
    }

    @PostMapping("/sponsors/toggle/{id}")
    public String toggleSponsorStatus(@PathVariable Long id) {
        com.viajes.viajes.model.Sponsor sponsor = sponsorRepository.findById(id).orElse(null);
        if (sponsor != null) {
            sponsor.setActivo(!sponsor.isActivo());
            sponsorRepository.save(sponsor);
        }
        return "redirect:/admin/sponsors?successStatus";
    }

    @GetMapping("/sponsors/editar/{id}")
    public String showEditSponsorForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        
        com.viajes.viajes.model.Sponsor sponsor = sponsorRepository.findById(id).orElse(null);
        if (sponsor == null) {
            return "redirect:/admin/sponsors?errorNotFound";
        }
        
        model.addAttribute("sponsor", sponsor);
        return "admin-editar-sponsor";
    }

    @PostMapping("/sponsors/editar/{id}")
    public String updateSponsor(@PathVariable Long id, 
                                @ModelAttribute("sponsor") com.viajes.viajes.model.Sponsor sponsorUpdates,
                                @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo) {
        
        com.viajes.viajes.model.Sponsor existingSponsor = sponsorRepository.findById(id).orElse(null);
        if (existingSponsor != null) {
            existingSponsor.setDescripcion(sponsorUpdates.getDescripcion());
            // Si sube un nuevo logo, reemplazamos
            if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
                String fotoPath = fileStorageService.storeFile(fotoArchivo);
                existingSponsor.setLogoPath(fotoPath);
            }
            sponsorRepository.save(existingSponsor);
        }
        
        return "redirect:/admin/sponsors?successEdit";
    }

    // --- DESTINOS MANAGEMENT ---

    @GetMapping("/destinos")
    public String gestionarDestinos(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        
        List<Destino> destinos = destinoRepository.findAll();
        model.addAttribute("destinos", destinos);
        model.addAttribute("nuevoDestino", new Destino());
        return "admin-destinos";
    }

    @PostMapping("/destinos/guardar")
    public String saveDestino(@ModelAttribute("nuevoDestino") Destino destino,
                              @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo) {
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            String fotoPath = fileStorageService.storeFile(fotoArchivo);
            destino.setImagenPath(fotoPath);
        }
        destinoRepository.save(destino);
        return "redirect:/admin/destinos?successAdd";
    }

    @PostMapping("/destinos/toggle/{id}")
    public String toggleDestinoStatus(@PathVariable Long id) {
        Destino destino = destinoRepository.findById(id).orElse(null);
        if (destino != null) {
            destino.setActivo(!destino.isActivo());
            destinoRepository.save(destino);
        }
        return "redirect:/admin/destinos?successStatus";
    }

    @GetMapping("/destinos/editar/{id}")
    public String showEditDestinoForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        
        Destino destino = destinoRepository.findById(id).orElse(null);
        if (destino == null) {
            return "redirect:/admin/destinos?errorNotFound";
        }
        
        model.addAttribute("destino", destino);
        return "admin-editar-destino";
    }

    @PostMapping("/destinos/editar/{id}")
    public String updateDestino(@PathVariable Long id, 
                                @ModelAttribute("destino") Destino destinoUpdates,
                                @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo) {
        
        Destino existingDestino = destinoRepository.findById(id).orElse(null);
        if (existingDestino != null) {
            existingDestino.setNombre(destinoUpdates.getNombre());
            existingDestino.setDescripcion(destinoUpdates.getDescripcion());
            // Si sube una nueva foto, reemplazamos
            if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
                String fotoPath = fileStorageService.storeFile(fotoArchivo);
                existingDestino.setImagenPath(fotoPath);
            }
            destinoRepository.save(existingDestino);
        }
        
        return "redirect:/admin/destinos?successEdit";
    }

    // --- RUTAS MANAGEMENT ---

    @GetMapping("/rutas")
    public String gestionarRutas(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        
        java.util.Optional<com.viajes.viajes.model.Ruta> rutaActivaOpt = rutaService.getRutaActiva();
        
        if (rutaActivaOpt.isPresent()) {
            com.viajes.viajes.model.Ruta ruta = rutaActivaOpt.get();
            double progreso = rutaService.calcularProgreso();
            double[] coords = rutaService.calcularCoordenadasActuales(progreso);
            
            model.addAttribute("rutaActiva", ruta);
            model.addAttribute("progreso", progreso);
            model.addAttribute("latActual", coords[0]);
            model.addAttribute("lngActual", coords[1]);
        }
        
        List<com.viajes.viajes.model.Ruta> rutasHistory = rutaService.findAll();
        model.addAttribute("rutas", rutasHistory);
        model.addAttribute("puertos", rutaService.getPuertosDisponibles());
        
        return "admin-rutas";
    }

    @PostMapping("/rutas/iniciar")
    public String iniciarRuta(@RequestParam("origen") String origen, @RequestParam("destino") String destino) {
        rutaService.iniciarRuta(origen, destino);
        return "redirect:/admin/rutas?successAdd";
    }

    @PostMapping("/rutas/finalizar")
    public String finalizarRuta() {
        rutaService.finalizarRutaActiva();
        return "redirect:/admin/rutas?successStop";
    }
}

