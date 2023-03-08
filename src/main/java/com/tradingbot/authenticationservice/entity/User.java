package com.tradingbot.authenticationservice.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="users",
        uniqueConstraints = {@UniqueConstraint(
                name = "email_unique",
                columnNames = {"email"} )
})
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false, unique = true)
    private String encryptedPwd;
    @Column(nullable = false)
    private String provider;

    private String nickname;

    private LocalDateTime createdAt;

    @Builder
    public User(Long id, String name, String email, String provider, String nickname) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.nickname = nickname;
    }


    public User update(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }
}
