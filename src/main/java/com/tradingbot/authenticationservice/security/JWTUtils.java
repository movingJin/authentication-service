package com.tradingbot.authenticationservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tradingbot.authenticationservice.dto.UserDto;
import com.tradingbot.authenticationservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JWTUtils {
    private final Environment env;

    public String createToken(User user) {
        return JWT.create()
                .withSubject(user.getUserId())
                .withExpiresAt(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .sign(Algorithm.HMAC256(Base64.getDecoder().decode(env.getProperty("token.secret"))));
    }

//    public Authentication verifyAndGetAuthentication(String token) {
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("role", String.class));
//            return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
//        } catch (JwtException | IllegalArgumentException ignored) {
//            return null;
//        }
//    }
}