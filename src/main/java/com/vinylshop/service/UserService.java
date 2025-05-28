package com.vinylshop.service;

import com.vinylshop.dto.UserDto;
import com.vinylshop.entity.Cart;
import com.vinylshop.entity.User;
import com.vinylshop.mapper.UserMapper;
import com.vinylshop.repository.UserRepository;
import com.vinylshop.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User create(User user) {
        if (user.getCurrency() == null) {
            final Currency currency = Constants.DEFAULT_CURRENCY;
            user.setCurrency(currency);
        }

        final Cart cart = new Cart();
        cart.setUser(user);

        user.setCart(cart);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(x -> {
                    PreferredCurrencyHolder.setCurrency(x.getCurrency());
                    return x;
                })
                .map(userMapper::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Користувача з таким іменем не знайдено: " + username));
    }

}

