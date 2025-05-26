package com.vinylshop.service;

import com.vinylshop.dto.CartDto;
import com.vinylshop.dto.CartItemDto;
import com.vinylshop.dto.CartItemUpdateRequest;
import com.vinylshop.dto.PageDto;
import com.vinylshop.entity.Cart;
import com.vinylshop.entity.CartItem;
import com.vinylshop.entity.User;
import com.vinylshop.entity.Vinyl;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.CartMapper;
import com.vinylshop.repository.CartItemRepository;
import com.vinylshop.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final VinylService vinylService;
    private final CartMapper cartMapper;

    @Transactional
    Cart createForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCurrency(user.getCurrency());
        return cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Optional<Cart> findByUserEmail(String email) {
        return cartRepository.findByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<CartDto> getCartByUserEmailWithTotalPrice(String email) {
        return findByUserEmail(email)
                .map(x -> {
                    CartDto cartDto = withTotalPrice(cartMapper.toDto(x));
                    cartDto.setItems(x.getItems().stream()
                            .map(item -> {
                                CartItemDto dto = cartMapper.toDto(item);
                                dto.setTotalPrice(calculateCartItemTotalPrice(item));
                                return dto;
                            }).collect(Collectors.toList())
                    );
                    return cartDto;
                });
    }

    @Transactional
    public CartItemDto addItem(String email, Long vinylId) {
        Vinyl vinyl = vinylService.findById(vinylId)
                .orElseThrow(() -> new ResourceNotFoundException(("Vinyl not found: " + vinylId), vinylId, "Vinyl"));

        vinylService.checkVinylQuantity(vinyl, 1);

        Cart cart = findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(("Cart not found for user: " + email), email, "Cart"));

        CartItem existingItem = cart.getItems().stream()
                .filter(x -> Objects.equals(x.getVinyl().getId(), vinyl.getId()))
                .findFirst()
                .map(item -> {
                    vinylService.checkVinylQuantity(vinyl, item.getQuantity() + 1);
                    item.setQuantity(item.getQuantity() + 1);
                    return cartItemRepository.save(item);
                }).orElseGet(() -> {
                    CartItem createdItem = cartItemRepository.save(new CartItem(cart, vinyl, 1));
                    cart.getItems().add(createdItem);
                    return createdItem;
                });

        CartItemDto cartItemDto = cartMapper.toDto(existingItem);
        cartItemDto.setTotalPrice(calculateCartItemTotalPrice(existingItem));
        return cartItemDto;
    }

    @Transactional
    public CartItemDto updateItem(String email, Long vinylId, CartItemUpdateRequest request) {
        CartItem cartItem = cartItemRepository.findByVinylIdAndUserEmail(vinylId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for: " + vinylId, vinylId, "CartItem"));
        Vinyl vinyl = cartItem.getVinyl();

        if (request.getQuantity() != null) {
            vinylService.checkVinylQuantity(vinyl, request.getQuantity());
            cartItem.setQuantity(request.getQuantity());
            cartItem = cartItemRepository.save(cartItem);
        }

        CartItemDto cartItemDto = cartMapper.toDto(cartItem);
        cartItemDto.setTotalPrice(calculateCartItemTotalPrice(cartItem));
        return cartItemDto;
    }

    @Transactional
    public void clearCart(String email) {
        cartItemRepository.deleteByCartUserEmail(email);
    }

    @Transactional
    public void deleteAllByUserEmailAndVinylIds(String email, Iterable<Long> vinylIds) {
        cartItemRepository.deleteByCartUserEmailAndVinylIdIn(email, vinylIds);
    }

    @Transactional(readOnly = true)
    public CartDto withTotalPrice(CartDto dto) {
        dto.setTotalPrice(calculateTotalPrice(dto.getId()));
        return dto;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalPrice(Long cartId) {
        return Optional.ofNullable(cartItemRepository.calculateTotalPriceByCartId(cartId))
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateCartItemTotalPrice(CartItem cartItem) {
        return cartItem.getVinyl().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }

}
