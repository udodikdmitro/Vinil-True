package com.vinylshop.mapper;

import com.vinylshop.dto.RegisterRequest;
import com.vinylshop.dto.UserDto;
import com.vinylshop.entity.Role;
import com.vinylshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.stream.Stream;

@Mapper
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    User toEntity(RegisterRequest dto);

    UserDto toDto(User entity);

    Stream<UserDto> toDtoAll(Iterable<User> entities);

    Stream<String> mapRolesToStrings(Iterable<Role> iterable);

    default UserDetails toUserDetails(User entity) {
        return org.springframework.security.core.userdetails.User.withUsername(entity.getEmail())
                .roles(mapRolesToStrings(entity.getRoles()).toArray(String[]::new))
                .password(entity.getPassword())
                .build();
    }

}
