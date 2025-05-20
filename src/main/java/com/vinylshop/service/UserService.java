package com.vinylshop.service;

import com.vinylshop.dto.UserDto;
import com.vinylshop.mapper.UserMapper;
import com.vinylshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(userMapper::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Користувача з таким іменем не знайдено: " + username));
    }

}

