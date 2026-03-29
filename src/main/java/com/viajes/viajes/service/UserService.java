package com.viajes.viajes.service;

import com.viajes.viajes.model.User;
import com.viajes.viajes.model.UserDto;

public interface UserService {
    void saveUser(UserDto userDto);
    void saveAdmin(UserDto userDto);
    User findUserByEmail(String email);
    void updateUserProfile(String email, UserDto userDto);
}
