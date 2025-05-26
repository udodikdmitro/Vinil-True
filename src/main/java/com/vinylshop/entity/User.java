package com.vinylshop.entity;

import com.vinylshop.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Currency;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_email", columnNames = "email")
        }
)
public class User extends AuditableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", foreignKey = @ForeignKey(name = "fk_user_roles_users_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Column(nullable = false)
    private Currency currency;

    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Cart cart;

}
