package com.vinylshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vinylshop.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Role> roles;

    private String email;
    private String fullName;
    private String currency;

}
