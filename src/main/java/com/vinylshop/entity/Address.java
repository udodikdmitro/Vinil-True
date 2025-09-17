package com.vinylshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "city")
    private String city;

    @Column(nullable = false, name = "street")
    private String street;

    @Column(nullable = false, name = "house_number")
    private String houseNumber;

    @Column(nullable = false, name = "apartment")
    private String apartment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "method")
    private DeliveryMethod method;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ShippingProvider provider;

    @Column(nullable = false, name = "branch_number")
    private String branchNumber;

    @Column(nullable = false, name = "is_default")
    private boolean isDefault = false;

    @Column(nullable = false, name = "create_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, name = "update_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
