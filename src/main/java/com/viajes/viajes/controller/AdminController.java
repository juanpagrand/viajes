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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findUserByEmail(auth.getName());
        model.addAttribute("admin", admin);
        return "admin-dashboard";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("admin") UserDto userDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
}
