package com.viajes.viajes.controller;

import com.viajes.viajes.model.User;
import com.viajes.viajes.model.UserDto;
import com.viajes.viajes.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Handler para mostrar el formulario de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Handler para mostrar el formulario de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // Handler para procesar el registro
    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") UserDto userDto,
            BindingResult result,
            Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "Ya existe una cuenta registrada con ese correo electrónico");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }

        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        User existingUser = userService.findUserByEmail(email);
        if (existingUser == null) {
            model.addAttribute("error", "El correo electrónico no está registrado");
            return "forgot-password";
        }

        try {
            userService.generateResetPasswordCode(email);
            return "redirect:/reset-password?email=" + email + "&sent";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al enviar el código de confirmación: " + e.getMessage());
            return "forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "email", required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("email") String email,
                                      @RequestParam("code") String code,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      Model model) {
        model.addAttribute("email", email);
        
        if (password == null || password.isEmpty()) {
            model.addAttribute("error", "La contraseña no puede estar vacía");
            return "reset-password";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "reset-password";
        }

        try {
            userService.resetPassword(email, code, password);
            return "redirect:/login?resetSuccess";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "reset-password";
        }
    }
}
