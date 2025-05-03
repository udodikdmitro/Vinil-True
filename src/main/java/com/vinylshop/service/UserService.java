package com.vinylshop.service;

import com.vinylshop.dto.UserDto;
import com.vinylshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getEmail(), user.getFullName(), user.getRoles()))
                .toList();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

