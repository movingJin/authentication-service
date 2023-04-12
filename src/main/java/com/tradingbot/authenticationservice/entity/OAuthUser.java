package com.tradingbot.authenticationservice.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="oauth_user",
        uniqueConstraints = {@UniqueConstraint(
                name = "userId_unique",
                columnNames = {"userId"} )
})
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false, unique = true)
    private String password;

    @Builder
    public OAuthUser(Long id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public OAuthUser(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
