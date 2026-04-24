package com.viajes.viajes.service;

import com.viajes.viajes.model.User;
import com.viajes.viajes.model.UserDto;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    void saveAdmin(UserDto userDto);
    User findUserByEmail(String email);
    void updateUserProfile(String email, UserDto userDto);
    List<User> findAllUsers();
    User findUserById(Long id);
    void toggleUserStatus(Long id);
    void updateUserById(Long id, UserDto userDto);
}
