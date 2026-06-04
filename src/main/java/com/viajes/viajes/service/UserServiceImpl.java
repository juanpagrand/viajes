package com.viajes.viajes.service;

import com.viajes.viajes.model.Role;
import com.viajes.viajes.model.User;
import com.viajes.viajes.model.UserDto;
import com.viajes.viajes.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setNombre(userDto.getNombre());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        // By default all registrations are USER
        user.setRole(Role.ROLE_USER);
        
        userRepository.save(user);
        
        // Enviar correo de confirmación de registro
        emailService.sendRegistrationEmail(user.getEmail(), user.getNombre());
    }

    @Override
    public void saveAdmin(UserDto userDto) {
        User user = new User();
        user.setNombre(userDto.getNombre());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.ROLE_ADMIN);
        
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void updateUserProfile(String email, UserDto userDto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setNombre(userDto.getNombre());
        user.setDescripcion(userDto.getDescripcion());
        user.setFoto(userDto.getFoto());
        // For admin, we don't automatically update email from profile to avoid losing login context without extra checks,
        // but if requested, we can do it. For now, we update it if provided.
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActivo(!user.isActivo());
        userRepository.save(user);
    }

    @Override
    public void updateUserById(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setNombre(userDto.getNombre());
        user.setDescripcion(userDto.getDescripcion());
        user.setFoto(userDto.getFoto());
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
    }

    @Override
    public void generateResetPasswordCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Generar un código de 6 dígitos aleatorio
        java.util.Random random = new java.util.Random();
        int codeInt = 100000 + random.nextInt(900000);
        String code = String.valueOf(codeInt);
        
        user.setConfirmationCode(code);
        user.setConfirmationCodeExpiresAt(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        
        // Enviar correo electrónico
        emailService.sendPasswordResetEmail(user.getEmail(), code);
    }

    @Override
    public boolean verifyResetCode(String email, String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        }
        
        String savedCode = user.getConfirmationCode();
        java.time.LocalDateTime expiresAt = user.getConfirmationCodeExpiresAt();
        
        if (savedCode == null || expiresAt == null) {
            return false;
        }
        
        return savedCode.equals(code) && expiresAt.isAfter(java.time.LocalDateTime.now());
    }

    @Override
    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!verifyResetCode(email, code)) {
            throw new RuntimeException("El código de confirmación es inválido o ha expirado");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        // Limpiar el código usado
        user.setConfirmationCode(null);
        user.setConfirmationCodeExpiresAt(null);
        userRepository.save(user);
    }
}
