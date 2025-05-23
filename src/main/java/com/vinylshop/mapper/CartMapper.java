package com.vinylshop.mapper;

import com.vinylshop.dto.CartDto;
import com.vinylshop.dto.CartItemDto;
import com.vinylshop.entity.Cart;
import com.vinylshop.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {VinylMapper.class})
public interface CartMapper {

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    CartDto toDto(Cart entity);

    @Mapping(target = "totalPrice", ignore = true)
    CartItemDto toDto(CartItem entity);

}
