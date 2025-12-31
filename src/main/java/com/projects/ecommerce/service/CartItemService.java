package com.projects.ecommerce.service;

import com.projects.ecommerce.dto.CartItemDto;
import com.projects.ecommerce.dto.request.AddCartItemDto;
import com.projects.ecommerce.dto.request.CheckoutPreviewRequest;
import com.projects.ecommerce.dto.response.CheckoutPreviewResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartItemService {
    CartItemDto addProductToCart(@Valid AddCartItemDto dto);

    Page<CartItemDto> getCartItems(Pageable pageable);

    void removeCartItem(Long cartItemId);

    long getCartCount();

    CheckoutPreviewResponse preview(CheckoutPreviewRequest request);

    CartItemDto updateQuantity(Long id, Integer quantity);

     Long clearCart();
}
