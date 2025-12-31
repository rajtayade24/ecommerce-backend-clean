package com.projects.ecommerce.service.impl;

import com.projects.ecommerce.dto.CartItemDto;
import com.projects.ecommerce.dto.ProductDto;
import com.projects.ecommerce.dto.request.AddCartItemDto;
import com.projects.ecommerce.dto.request.CheckoutPreviewRequest;
import com.projects.ecommerce.dto.response.CheckoutPreviewResponse;
import com.projects.ecommerce.dto.response.OrderItemResponse;
import com.projects.ecommerce.dto.response.OrderResponse;
import com.projects.ecommerce.entity.CartItem;
import com.projects.ecommerce.entity.Product;
import com.projects.ecommerce.entity.ProductVariant;
import com.projects.ecommerce.entity.User;
import com.projects.ecommerce.repository.CartItemRepository;
import com.projects.ecommerce.repository.ProductRepository;
import com.projects.ecommerce.repository.ProductVatiantRepository;
import com.projects.ecommerce.repository.UserRepository;
import com.projects.ecommerce.service.CartItemService;
import com.projects.ecommerce.util.MapperUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductVatiantRepository vatiantRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MapperUtil mapper;

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication user: " + auth);
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            // Handle guest: return null, create temporary cart, or throw business exception
            throw new RuntimeException("User must be logged in to add to cart");
        }
        String identifier = auth.getName();
        return userRepository.findByEmailOrMobile(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("User not found: " + identifier));
    }

    @Override
    @Transactional
    public CartItemDto addProductToCart(@Valid AddCartItemDto dto) {
        User user = getLoggedInUser();
        System.out.println("user is: " + user);

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 2. Validate Product Variant
        ProductVariant variant = vatiantRepository.findById(dto.getVariantId())
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        // Check existing item (by user + variant) OR (user + product if variant is null)
        Optional<CartItem> existingOpt = (variant != null)
                ? cartItemRepository.findByUserAndVariant(user, variant)
                : cartItemRepository.findByUserAndProduct(user, product);

        if (existingOpt.isPresent()) {
            CartItem existing = existingOpt.get();
//            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
            existing.setQuantity(dto.getQuantity());
            CartItem saved = cartItemRepository.save(existing);
            return mapper.mapCartItemToCartItemDto(saved);
        }

        CartItem newItem = CartItem.builder()
                .user(user)
                .product(product)
                .variant(variant)
                .quantity(dto.getQuantity())
                .price(variant.getPrice()) // store unit price in CartItem.price (as per your entity)
                .createdAt(OffsetDateTime.now())
                .build();

        CartItem saved = cartItemRepository.save(newItem);
        return mapper.mapCartItemToCartItemDto(saved);
    }

    @Override
    public Page<CartItemDto> getCartItems(Pageable pageable) {
        User user = getLoggedInUser();

        System.out.println("user is: " + user);
        Page<CartItem> page = cartItemRepository.findByUser(user, pageable);
        return page.map(mapper::mapCartItemToCartItemDto);
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartItemId) {
        User user = getLoggedInUser();
        System.out.println("user is: " + user);
        CartItem item = cartItemRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new RuntimeException("Cart item not found or not yours"));
        cartItemRepository.delete(item);
    }

    @Override
    public long getCartCount() {
        User user = getLoggedInUser();
        System.out.println("user is: " + user);
        return cartItemRepository.countByUser(user);
    }


    @Override
    public CartItemDto updateQuantity(Long id, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cart item not found"
                ));

        ProductVariant variant = cartItem.getVariant();
        if (variant.getStock() < quantity) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only " + variant.getStock() + " items left in stock"
            );
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return mapper.mapCartItemToCartItemDto(cartItem);
    }

    @Override
    @Transactional  
    public Long clearCart() {

        User user = getLoggedInUser();
        long count = cartItemRepository.countByUser(user);
        
        cartItemRepository.deleteByUser(user);

        return count; // number of deleted items
    }

    @Override
    public CheckoutPreviewResponse preview(CheckoutPreviewRequest request) {

        double subtotal = 0;
        List<OrderItemResponse> responses = new ArrayList<>();

        for (CheckoutPreviewRequest.ItemRequest item : request.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductVariant variant = vatiantRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            if (!variant.getProduct().getId().equals(product.getId())) {
                throw new RuntimeException("Variant does not belong to product");
            }

            if (variant.getStock() < item.getQuantity()) {
                throw new RuntimeException("Out of stock");
            }

            double itemTotal = variant.getPrice() * item.getQuantity();
            subtotal += itemTotal;

            // Images
            String baseUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .build()
                    .toUriString();

            String image = product.getImages().isEmpty() ? null : baseUrl + product.getImages().get(0);

            responses.add(OrderItemResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .image(image)
                    .variantId(variant.getId())
                    .variantLabel(variant.getValue() + variant.getUnit())
                    .unitPrice(BigDecimal.valueOf(variant.getPrice()))
                    .quantity(item.getQuantity())
                    .lineTotal(BigDecimal.valueOf(itemTotal))
                    .build());
        }

        double deliveryFee = subtotal < 200 ? 40 : 0;
        double total = subtotal + deliveryFee;

        return CheckoutPreviewResponse.builder()
                .items(responses)
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .total(total)
                .build();
    }

  
}

