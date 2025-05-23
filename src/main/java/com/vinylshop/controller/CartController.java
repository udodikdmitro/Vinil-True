package com.vinylshop.controller;

import com.vinylshop.dto.*;
import com.vinylshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/v1/me/cart")
    public ResponseEntity<CartDto> getCart(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
            Authentication authentication
    ) {
        return ResponseEntity.of(cartService.getCartByUserEmailWithTotalPrice(authentication.getName(), pageable));
    }

    @PostMapping("/v1/me/cart/items/{vinylId}")
    public ResponseEntity<CartItemDto> addItem(
            @PathVariable Long vinylId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(cartService.addItem(authentication.getName(), vinylId));
    }

    @PatchMapping("/v1/me/cart/items/{vinylId}")
    public ResponseEntity<CartItemDto> updateItem(
            @PathVariable Long vinylId,
            @RequestBody CartItemUpdateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(cartService.updateItem(authentication.getName(), vinylId, request));
    }

    @DeleteMapping("/v1/me/cart/items")
    public ResponseEntity<String> deleteAllByVinylIds(
            @RequestBody List<Long> vinylIds,
            Authentication authentication
    ) {
        cartService.deleteAllByUserEmailAndVinylIds(authentication.getName(), vinylIds);
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/v1/me/cart/clear")
    public ResponseEntity<String> deleteAll(
            Authentication authentication
    ) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.ok("OK");
    }

}
