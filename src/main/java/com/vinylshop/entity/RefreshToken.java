package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "refresh_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_refresh_token_user_id", columnNames = "user_id"),
                @UniqueConstraint(name = "uq_refresh_token_token", columnNames = "token")
        }
)
public class RefreshToken extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String token;

    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
