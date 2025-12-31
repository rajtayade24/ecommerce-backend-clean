package com.projects.ecommerce.controller.publicapi;


import com.projects.ecommerce.dto.CartItemDto;
import com.projects.ecommerce.dto.request.AddCartItemDto;
import com.projects.ecommerce.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://localhost:8080",
        "http://127.0.0.1:5500",
        "http://localhost:5173",
        "http://10.91.2.29:5173",
        "http://10.91.2.29:5173/instagram-clone",
        "https://social-media-frontend-nbdo.vercel.app",
        "*"
})
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<CartItemDto> addToCart(@RequestBody AddCartItemDto dto) {
        CartItemDto saved = cartItemService.addProductToCart(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<Page<CartItemDto>> getCart(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CartItemDto> carts = cartItemService.getCartItems(pageable);
        return ResponseEntity.ok(carts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCart(@PathVariable("id") Long id) {
        cartItemService.removeCartItem(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/count")
    public ResponseEntity<Long> getCartCount() {
        long count = cartItemService.getCartCount();
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemDto> updateQuantity(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body
    ) {
        Integer quantity = body.get("quantity");

        if (quantity == null || quantity < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be >= 1"
            );
        }

        return ResponseEntity.ok(cartItemService.updateQuantity(id, quantity));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Long> deleteCart() {
        return ResponseEntity.ok(cartItemService.clearCart());
    }
}
