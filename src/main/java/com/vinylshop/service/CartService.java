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
    public Optional<CartDto> getCartByUserEmailWithTotalPrice(String email, Pageable pageable) {
        return findByUserEmail(email)
                .map(x -> withTotalPrice(cartMapper.toDto(x)))
                .map(x -> {
                    x.setItems(getCartItemPage(email, pageable));
                    return x;
                });
    }

    @Transactional(readOnly = true)
    public PageDto<CartItemDto> getCartItemPage(String email, Pageable pageable) {
        return new PageDto<>(cartItemRepository.findAllByCartUserEmail(email, pageable)
                .map(x -> {
                    CartItemDto dto = cartMapper.toDto(x);
                    dto.setTotalPrice(calculateCartItemTotalPrice(x));
                    return dto;
                }));
    }

    @Transactional
    public CartItemDto addItem(String email, Long vinylId) {
        Cart cart = findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(("Cart not found for user: " + email), email, "Cart"));

        Vinyl vinyl = vinylService.findById(vinylId)
                .orElseThrow(() -> new ResourceNotFoundException(("Vinyl not found: " + vinylId), vinylId, "Vinyl"));

        CartItem existingItem = cart.getItems().stream()
                .filter(x -> Objects.equals(x.getVinyl().getId(), vinyl.getId()))
                .findFirst()
                .map(item -> {
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

        if (request.getQuantity() != null) {
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
