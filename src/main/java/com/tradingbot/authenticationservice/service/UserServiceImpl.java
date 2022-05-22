package com.tradingbot.authenticationservice.service;

import com.tradingbot.authenticationservice.dto.UserDto;
import com.tradingbot.authenticationservice.entity.User;
import com.tradingbot.authenticationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserDto userDto) {
        User user = null;
        if(userRepository.findByEmail(userDto.getEmail()).isEmpty()) {
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            user = mapper.map(userDto, User.class);
            user.setUserId(UUID.randomUUID().toString());
            user.setEncryptedPwd(passwordEncoder.encode(userDto.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new ModelMapper().map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new ModelMapper().map(user, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getEncryptedPwd(),
                true, true, true, true, new ArrayList<>());
    }

    @Override
    public void updateUser(UserDto requestUser) {
        User user = userRepository.findByEmail(requestUser.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(requestUser.getEmail()));

        user.setEncryptedPwd(passwordEncoder.encode(requestUser.getPassword()));
        userRepository.save(user);
    }
}
