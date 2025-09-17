package com.vinylshop.dto;

import com.vinylshop.entity.Address;
import com.vinylshop.entity.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserDto {
    private Long id;

    private String email;

    private String passwordHash;

    private String fullName;

    private Set<Role> roles;

}
