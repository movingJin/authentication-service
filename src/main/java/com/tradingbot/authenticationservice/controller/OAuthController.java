package com.tradingbot.authenticationservice.controller;

import com.tradingbot.authenticationservice.dto.IdTokenRequestDto;
import com.tradingbot.authenticationservice.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
//    @PostMapping("/login")
//    public String oauthLoginInfo(Authentication authentication){
//        log.debug(authentication.toString());
//        //oAuth2User.toString() 예시 : Name: [2346930276], Granted Authorities: [[USER]], User Attributes: [{id=2346930276, provider=kakao, name=김준우, email=bababoll@naver.com}]
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        //attributes.toString() 예시 : {id=2346930276, provider=kakao, name=김준우, email=bababoll@naver.com}
//        log.debug(oAuth2User.getAttributes().toString());
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        return attributes.toString();
//    }

    @PostMapping("/login")
    public ResponseEntity LoginWithGoogleOauth2(@RequestBody IdTokenRequestDto requestBody, HttpServletResponse response) {
        String authToken = accountService.loginOAuthGoogle(requestBody);
        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }
}