package com.vinylshop.service;

import com.vinylshop.dto.CartDto;
import com.vinylshop.dto.CartItemDto;
import com.vinylshop.dto.CartItemUpdateRequest;
import com.vinylshop.entity.*;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.CartMapper;
import com.vinylshop.repository.CartItemRepository;
import com.vinylshop.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CartMapper cartMapper;

    @Transactional(readOnly = true)
    public Optional<Cart> findByUserEmail(String email) {
        return cartRepository.findByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<CartDto> getCartByUserEmailWithTotalPrice(String email) {
        return findByUserEmail(email).map(this::toDtoWithTotalPrice);
    }

    @Transactional
    public CartItemDto addItem(String email, Long productId) {
        Cart cart = findByUserEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(("Cart not found for user: " + email), email, "Cart"));

        Product product = productService.getByIdOrThrow(productId);

        productService.checkQuantity(product, 1);

        CartItem existingItem = cart.getItems().stream()
            .filter(x -> Objects.equals(x.getProduct().getId(), productId))
            .findFirst()
            .map(item -> {
                productService.checkQuantity(product, item.getQuantity() + 1);
                item.setQuantity(item.getQuantity() + 1);
                return cartItemRepository.save(item);
            }).orElseGet(() -> {
                CartItem createdItem = cartItemRepository.save(new CartItem(null, cart, product, 1));
                cart.getItems().add(createdItem);
                return createdItem;
            });

        final Currency currency = cart.getCurrency();
        final CartItemDto cartItemDto = cartMapper.toDto(existingItem);
        cartItemDto.setTotalPrice(calculateCartItemTotalPrice(existingItem, currency, null));
        return cartItemDto;
    }

    @Transactional
    public CartItemDto updateItem(String email, Long vinylId, CartItemUpdateRequest request) {
        CartItem cartItem = cartItemRepository.findByProductIdAndUserEmail(vinylId, email)
            .orElseThrow(() ->
                new ResourceNotFoundException("Cart item not found for: " + vinylId, vinylId, "CartItem"));
        Product product = cartItem.getProduct();

        if (request.getQuantity() != null) {
            productService.checkQuantity(product, request.getQuantity());
            cartItem.setQuantity(request.getQuantity());
            cartItem = cartItemRepository.save(cartItem);
        }

        final Currency currency = cartItem.getCart().getCurrency();
        final CartItemDto cartItemDto = cartMapper.toDto(cartItem);
        cartItemDto.setTotalPrice(calculateCartItemTotalPrice(cartItem, currency, null));
        return cartItemDto;
    }

    @Transactional
    public void clearCart(String email) {
        cartItemRepository.deleteByCartUserEmail(email);
    }

    @Transactional
    public void deleteAllByUserEmailAndVinylIds(String email, Iterable<Long> vinylIds) {
        cartItemRepository.deleteByCartUserEmailAndProductIdIn(email, vinylIds);
    }

    @Transactional(readOnly = true)
    public CartDto toDtoWithTotalPrice(Cart cart) {
        if (cart == null) return null;
        final CartDto cartDto = cartMapper.toDto(cart);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            cartDto.setTotalPrice(BigDecimal.ZERO);
            cartDto.setItems(new ArrayList<>());
        } else {
            final Map<Long, BigDecimal> itemTotalPrices = new HashMap<>(cart.getItems().size(), 1.0f);
            cartDto.setTotalPrice(calculateTotalPrice(cart, itemTotalPrices));
            cartDto.setItems(cart.getItems().stream()
                    .map(x -> toDtoWithTotalPrice(x, itemTotalPrices.get(x.getId())))
                    .collect(Collectors.toList()));
        }
        return cartDto;
    }

    @Transactional(readOnly = true)
    public CartItemDto toDtoWithTotalPrice(CartItem item, BigDecimal itemTotalPrice) {
        CartItemDto cartDto = cartMapper.toDto(item);
        cartDto.setTotalPrice(itemTotalPrice);
        return cartDto;
    }

    public BigDecimal calculateTotalPrice(Cart cart, Map<Long, BigDecimal> totalPrices) {
        BigDecimal total = BigDecimal.ZERO;
        if (cart != null && cart.getItems() != null) {
            final Currency currency = cart.getCurrency();
            for(CartItem item : cart.getItems()) {
                BigDecimal itemTotalPrice = calculateCartItemTotalPrice(item, currency, totalPrices);
                total = total.add(itemTotalPrice);
            }
        }
        return total;
    }

    public BigDecimal calculateCartItemTotalPrice(CartItem item, Currency currency, Map<Long, BigDecimal> itemTotalPrices) {
        final int quantity = item.getQuantity();
        final Currency itemCurrency = item.getCart().getCurrency();
        BigDecimal price = item.getProduct().getPrice();

        if (!Objects.equals(currency, itemCurrency)) {
            // Add currency conversion logic here if needed
        }

        BigDecimal itemTotalPrice = price.multiply(BigDecimal.valueOf(quantity));
        if (itemTotalPrices != null) itemTotalPrices.put(item.getId(), itemTotalPrice);
        return itemTotalPrice;
    }

}
