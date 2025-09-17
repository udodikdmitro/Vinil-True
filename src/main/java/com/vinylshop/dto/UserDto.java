package com.vinylshop.dto;

import com.vinylshop.entity.Role;
import lombok.*;
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
