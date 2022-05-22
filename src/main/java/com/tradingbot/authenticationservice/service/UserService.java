package com.tradingbot.authenticationservice.service;

import com.tradingbot.authenticationservice.dto.UserDto;
import com.tradingbot.authenticationservice.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    UserDto getUserByEmail(String email);
    void updateUser(UserDto requestUser);
}
