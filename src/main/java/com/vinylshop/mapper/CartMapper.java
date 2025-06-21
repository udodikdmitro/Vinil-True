package com.vinylshop.mapper;

import com.vinylshop.dto.CartDto;
import com.vinylshop.dto.CartItemDto;
import com.vinylshop.entity.Cart;
import com.vinylshop.entity.CartItem;
import com.vinylshop.util.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {VinylMapper.class})
public interface CartMapper {

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "currency", expression = "java(com.vinylshop.util.Constants.DEFAULT_CURRENCY.getCurrencyCode())")
    CartDto toDto(Cart entity);

    @Mapping(target = "totalPrice", ignore = true)
    CartItemDto toDto(CartItem entity);

}
