package com.viajes.viajes.service;

import com.viajes.viajes.model.Role;
import com.viajes.viajes.model.User;
import com.viajes.viajes.model.UserDto;
import com.viajes.viajes.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
