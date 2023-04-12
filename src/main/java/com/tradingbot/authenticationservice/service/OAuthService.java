package com.tradingbot.authenticationservice.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.tradingbot.authenticationservice.dto.IdTokenRequestDto;
import com.tradingbot.authenticationservice.dto.UserDto;
import com.tradingbot.authenticationservice.entity.OAuthUser;
import com.tradingbot.authenticationservice.entity.User;
import com.tradingbot.authenticationservice.repository.OAuthUserRepository;
import com.tradingbot.authenticationservice.repository.UserRepository;
import com.tradingbot.authenticationservice.security.JWTUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Service
public class OAuthService {

    private final UserRepository userRepository;
    private final OAuthUserRepository oAuthUserRepository;
    private final UserService userService;
    private final JWTUtils jwtUtils;
    private final GoogleIdTokenVerifier verifier;
    private final BCryptPasswordEncoder passwordEncoder;

    public OAuthService(@Value("${spring.security.oauth2.client.registration.google.client-id}")
                                String clientId,
                        UserRepository userRepository,
                        OAuthUserRepository oAuthUserRepository,
                        UserService userService,
                        JWTUtils jwtUtils,
                        BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.oAuthUserRepository = oAuthUserRepository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserDto loginOAuthGoogle(IdTokenRequestDto requestBody) {
        User user = verifyIDToken(requestBody.getIdToken());
        if (user == null) {
            throw new IllegalArgumentException();
        }
        user = createOrUpdateUser(user);
        UserDto userDto = new ModelMapper().map(user, UserDto.class);
        oAuthUserRepository.findByUserId(user.getUserId()).ifPresent(oAuthUser -> userDto.setPassword(oAuthUser.getPassword()));
        return userDto;
    }

    @Transactional
    public User createOrUpdateUser(User user) {
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (existingUser == null) {
            OAuthUser oAuthUser = new OAuthUser(UUID.randomUUID().toString(), getRamdomPassword(10));

            user.setUserId(oAuthUser.getUserId());
            user.setEncryptedPwd(passwordEncoder.encode(oAuthUser.getPassword()));
            user.setCreatedAt(LocalDateTime.now());

            oAuthUserRepository.save(oAuthUser);
            userRepository.save(user);
            return user;
        }
        existingUser.setName(user.getName());
        userRepository.save(existingUser);
        return existingUser;
    }

    private String getRamdomPassword(int size) {
        char[] charSet = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '!', '@', '#', '$', '%', '^', '&' };

        StringBuilder sb = new StringBuilder();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        int len = charSet.length;
        for (int i=0; i<size; i++) {
            sb.append(charSet[sr.nextInt(len)]);
        }

        return sb.toString();
    }

    private User verifyIDToken(String idToken) {
        try {
            GoogleIdToken idTokenObj = verifier.verify(idToken);
            if (idTokenObj == null) {
                return null;
            }
            GoogleIdToken.Payload payload = idTokenObj.getPayload();
            String name = (String) payload.get("name");
            String email = payload.getEmail();
            String pictureUrl = (String) payload.get("picture");

            return new User(name, email);
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }
    }
}
