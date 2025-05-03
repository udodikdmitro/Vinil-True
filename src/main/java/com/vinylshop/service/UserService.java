package com.vinylshop.service;

import com.vinylshop.dto.UserDto;
import com.vinylshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getEmail(), user.getFullName(), user.getRoles()))
                .toList();
    }

    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(entity -> new UserDto(
                        entity.getId(),
                        entity.getEmail(),
                        entity.getFullName(),
                        entity.getRoles()
                ));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(entity -> {
                    return User.withUsername(username)
                            .roles(
                                    entity.getRoles().stream()
                                            .map(Enum::name)
                                            .toArray(String[]::new)
                            )
                            .password(entity.getPassword())
                            .build();
                }).orElseThrow(() -> new UsernameNotFoundException("Користувача з таким іменем не знайдено: " + username));
    }

}

