package com.tradingbot.authenticationservice.repository;

import com.tradingbot.authenticationservice.entity.OAuthUser;
import com.tradingbot.authenticationservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {

    Optional<OAuthUser> findByUserId(String email);
}
