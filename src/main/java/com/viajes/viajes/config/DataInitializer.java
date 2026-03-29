package com.viajes.viajes.config;

import com.viajes.viajes.model.Role;
import com.viajes.viajes.model.User;
import com.viajes.viajes.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@gmail.com";
        Optional<User> adminOptional = userRepository.findByEmail(adminEmail);

        if (adminOptional.isEmpty()) {
            User admin = new User();
            admin.setNombre("David (Capitán)");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("123456789"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setDescripcion("Máster Mariner & Líder de Expediciones. Más de 30 años navegando las aguas más traicioneras del planeta.");
            admin.setFoto("/images/2969933.png");
            
            userRepository.save(admin);
            System.out.println("Admin user created automatically.");
        }
    }
}
