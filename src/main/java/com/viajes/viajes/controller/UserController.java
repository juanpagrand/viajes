package com.viajes.viajes.controller;

import com.viajes.viajes.model.User;
import com.viajes.viajes.repository.BitacoraRepository;
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

    public UserController(UserService userService, BitacoraRepository bitacoraRepository) {
        this.userService = userService;
        this.bitacoraRepository = bitacoraRepository;
    }

    @GetMapping("/user")
    public String userDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addAttribute("usuario", user);
        model.addAttribute("bitacoras", bitacoraRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha")));
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
}
