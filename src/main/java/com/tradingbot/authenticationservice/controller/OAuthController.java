package com.tradingbot.authenticationservice.controller;

import com.tradingbot.authenticationservice.dto.IdTokenRequestDto;
import com.tradingbot.authenticationservice.dto.UserDto;
import com.tradingbot.authenticationservice.entity.User;
import com.tradingbot.authenticationservice.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.google.common.net.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {
    private final OAuthService accountService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> LoginWithGoogleOauth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
        User user = accountService.verifyIDToken(requestBody.getIdToken());

        if(user != null){
            return ResponseEntity.status(HttpStatus.OK).body(new ModelMapper().map(user, UserDto.class));
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}