package com.tradingbot.authenticationservice.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.tradingbot.authenticationservice.dto.IdTokenRequestDto;
import com.tradingbot.authenticationservice.entity.User;
import com.tradingbot.authenticationservice.repository.UserRepository;
import com.tradingbot.authenticationservice.security.JWTUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
public class OAuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JWTUtils jwtUtils;
    private final GoogleIdTokenVerifier verifier;

    public OAuthService(@Value("${spring.security.oauth2.client.registration.google.client-id}")
                                String clientId,
                        UserRepository userRepository,
                        UserService userService,
                        JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public String loginOAuthGoogle(IdTokenRequestDto requestBody) {
        User user = verifyIDToken(requestBody.getIdToken());
        if (user == null) {
            throw new IllegalArgumentException();
        }
        user = createOrUpdateUser(user);
        return jwtUtils.createToken(user);
    }

    @Transactional
    public User createOrUpdateUser(User account) {
        User existingUser = userRepository.findByEmail(account.getEmail()).orElse(null);
        if (existingUser == null) {
            account.setUserId(UUID.randomUUID().toString());
            account.setEncryptedPwd("testPassword");
            account.setCreatedAt(LocalDateTime.now());

            userRepository.save(account);
            return account;
        }
        existingUser.setName(account.getName());
        userRepository.save(existingUser);
        return existingUser;
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
