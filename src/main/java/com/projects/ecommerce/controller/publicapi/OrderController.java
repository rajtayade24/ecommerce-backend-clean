package com.projects.ecommerce.controller.publicapi;


import com.projects.ecommerce.dto.request.CheckoutPreviewRequest;
import com.projects.ecommerce.dto.request.OrderRequest;
import com.projects.ecommerce.dto.response.CheckoutPreviewResponse;
import com.projects.ecommerce.dto.response.OrderResponse;
import com.projects.ecommerce.dto.response.StripeResponse;
import com.projects.ecommerce.service.CartItemService;
import com.projects.ecommerce.service.OrderService;
import com.projects.ecommerce.service.StripeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final StripeService stripeService;
    private final OrderService orderService;
    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<StripeResponse> checkOutProducts(@RequestBody OrderRequest orderRequest) {
        StripeResponse response = stripeService.checkoutProducts(orderRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<StripeResponse> verifyPayment(@RequestParam String sessionId) {
        StripeResponse response = stripeService.verifyPayment(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getUserOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderResponse> page1 = orderService.getUserOrders(pageable);
        return ResponseEntity.ok(page1);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelUserOrder(@PathVariable Long id) {
        OrderResponse response = orderService.cancelUserOrder(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/preview")         // Never use @GetMapping with @RequestBody
    public ResponseEntity<CheckoutPreviewResponse> preview(
            @RequestBody CheckoutPreviewRequest items
    ) {
        return ResponseEntity.ok(cartItemService.preview(items));
    }

}
