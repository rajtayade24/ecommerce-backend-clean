package com.projects.ecommerce.service.impl;

import com.projects.ecommerce.dto.request.OrderRequest;
import com.projects.ecommerce.dto.response.StripeResponse;
import com.projects.ecommerce.entity.*;
import com.projects.ecommerce.enums.OrderStatusType;
import com.projects.ecommerce.enums.SuperCoinReason;
import com.projects.ecommerce.repository.OrderRepository;
import com.projects.ecommerce.repository.ProductVatiantRepository;
import com.projects.ecommerce.repository.SuperCoinTransactionRepository;
import com.projects.ecommerce.service.StripeService;
import com.projects.ecommerce.service.UserService;
import com.projects.ecommerce.util.MapperUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {


    private final OrderRepository orderRepository;
    private final ProductVatiantRepository productVatiantRepository;
    private final UserService service;
    private final SuperCoinTransactionRepository superCoinTransactionRepository;
    private final MapperUtil mapper;

    private static final int COINS_PER_AMOUNT = 100; // 1 coin per 100 units

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Override
    @Transactional
    public StripeResponse checkoutProducts(OrderRequest orderRequest) {
        Stripe.apiKey = secretKey;

        User user = service.getCurrentUser();

        try {
            // 1) Build & persist a PENDING order (snapshot items)
            Order order = mapper.toOrderEntity(orderRequest, user);
            order.setStatus(OrderStatusType.PENDING);
            order = orderRepository.save(order); // now order.id exists

            // 2) Build Stripe session with line items, add metadata orderId
            String currency = orderRequest.getCurrency() == null ? "usd" : orderRequest.getCurrency().toLowerCase();

            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:5500/payment-success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:5500/payment-cancel");

            // Add metadata - include orderId so we can lookup later
            Map<String, String> metadata = new HashMap<>();
            metadata.put("orderId", order.getId().toString());
            if (user != null)
                metadata.put("userId", user.getId().toString());
            paramsBuilder.putAllMetadata(metadata);

            // Add line items
            for (OrderItem it : order.getItems()) {
                long unitAmount = it.getUnitPrice().multiply(BigDecimal.valueOf(100)).longValue();

                SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData
                        .builder()
                        .setName(it.getProductName() == null ? "Product" : it.getProductName())
                        .build();

                SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(currency)
                        .setUnitAmount(unitAmount)
                        .setProductData(productData)
                        .build();

                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(it.getQuantity()))
                        .setPriceData(priceData)
                        .build();

                paramsBuilder.addLineItem(lineItem);
            }

            SessionCreateParams params = paramsBuilder.build();

            Session session = Session.create(params);

            // 3) Save stripe session id on order
            order.setStripeSessionId(session.getId());

            orderRepository.save(order);

//            int percentage = 1 + new Random().nextInt(5); // 1 to 5
//            int coins = (order.getTotalAmount().intValue() * percentage) / 100;
//
//            SuperCoinTransaction.builder()
//                    .amount()
//                    .reason(SuperCoinReason.ORDER_REWARD)
//                    .build();

            return StripeResponse.builder()
                    .status("SUCCESS")
                    .message("Payment session created")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe create session failed", e);
            // Optionally mark order as FAILED or delete draft order
            return StripeResponse.builder()
                    .status("FAILED")
                    .message(e.getMessage())
                    .build();
        } catch (Exception ex) {
            log.error("Unexpected error", ex);
            return StripeResponse.builder()
                    .status("ERROR")
                    .message(ex.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public StripeResponse verifyPayment(String sessionId) {
        log.info("Stripe payment verified: sessionId={}", sessionId);

        Stripe.apiKey = secretKey;
        try {
            Session session = Session.retrieve(sessionId);

            // extra safety
            // if ("paid".equals(session.getPaymentStatus())
            // && Session.Mode.PAYMENT.equals(session.getMode())) {

            // Stripe payment completed
            if (!"paid".equals(session.getPaymentStatus())) {
                return StripeResponse.builder()
                        .status("FAILED")
                        .message("Payment not completed")
                        .build();
            }

            // get orderId from metadata (preferred)
            String orderIdStr = session.getMetadata() != null ? session.getMetadata().get("orderId") : null;
            Order order = null;
            if (orderIdStr != null) {
                Long oid = Long.valueOf(orderIdStr);
                order = orderRepository.findById(oid).orElse(null);
            }

            // fallback: find by stripeSessionId
            if (order == null) {
                order = orderRepository.findByStripeSessionId(sessionId).orElse(null);
            }

            if (order == null) {
                // Could create order here from session metadata or return error
                return StripeResponse.builder()
                        .status("ERROR")
                        .message("Corresponding order not found")
                        .sessionId(sessionId)
                        .build();
            }

            // Idempotency: if already paid, return success
            if (order.getStatus() == com.projects.ecommerce.enums.OrderStatusType.PAID
                    || orderRepository.existsByStripePaymentIntentId(session.getPaymentIntent())) {
                return StripeResponse.builder()
                        .status("SUCCESS")
                        .message("Payment verified successfully")
                        .sessionId(sessionId)
                        .build();
            }

            // 1) Update order status + payment info
            order.setStatus(com.projects.ecommerce.enums.OrderStatusType.PAID);
            order.setStripePaymentIntentId(session.getPaymentIntent());
            order.setPaidAt(OffsetDateTime.now());

            orderRepository.save(order);

            // Reward coins AFTER payment
            rewardForOrder(order);

            // 2) Reduce stock for each item
            for (OrderItem it : order.getItems()) {
                if (it.getVariantId() != null) {
                    ProductVariant variant = productVatiantRepository.findById(it.getVariantId())
                            .orElseThrow(() -> new IllegalStateException("Variant not found: " + it.getVariantId()));

                    int available = variant.getStock() == null ? 0 : variant.getStock();
                    if (available < it.getQuantity()) {
                        // handle low-stock situation — could throw, place backorder, or mark partial
                        throw new IllegalStateException("Insufficient stock for variant: " + it.getVariantId());
                    }
                    variant.setStock(available - it.getQuantity());
                    productVatiantRepository.save(variant);
                } else {
                    // If no variant id, you can attempt to reduce product-level stock if you have
                    // it
                }
            }

            // 3) SuperCoin reward (IDEMPOTENT)
            boolean alreadyRewarded =
                    superCoinTransactionRepository.existsByUserIdAndReasonAndReferenceId(
                            order.getUser().getId(),
                            SuperCoinReason.ORDER_REWARD,
                            order.getId().toString()
                    );

            if (!alreadyRewarded) {

                double percentage = 1 + new Random().nextInt(5); // 1–5%
                int coins = Math.max(1,
                        order.getTotalAmount()
                                .multiply(BigDecimal.valueOf(percentage))
                                .divide(BigDecimal.valueOf(100), RoundingMode.DOWN)
                                .intValue()
                );
                SuperCoinTransaction txn = SuperCoinTransaction.builder()
                        .user(order.getUser())
                        .amount(coins) // positive = credit
                        .reason(SuperCoinReason.ORDER_REWARD)
                        .referenceId(order.getId().toString())
                        .createdAt(OffsetDateTime.now())
                        .build();

                superCoinTransactionRepository.save(txn);

                // OPTIONAL: update cached balance if you store it on User
                // user.setSuperCoins(user.getSuperCoins() + coins);
                // userRepository.save(user);
            }


            // 3) Save final order
            order = orderRepository.save(order);

            return StripeResponse.builder()
                    .status("SUCCESS")
                    .message("Payment verified successfully")
                    .sessionId(sessionId)
                    .build();

        } catch (StripeException e) {
            return StripeResponse.builder()
                    .status("ERROR")
                    .message(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public void rewardForOrder(Order order) {

        // Idempotency check
        if (superCoinTransactionRepository.existsByUserIdAndReasonAndReferenceId(
                order.getUser().getId(),
                SuperCoinReason.ORDER_REWARD,
                order.getId().toString()
        )) {
            return;
        }

        int coins = calculateCoins(order.getTotalAmount());

        if (coins <= 0) return;

        SuperCoinTransaction txn = SuperCoinTransaction.builder()
                .user(order.getUser())
                .amount(coins)
                .reason(SuperCoinReason.ORDER_REWARD)
                .referenceId(order.getId().toString())
                .build();

        superCoinTransactionRepository.save(txn);
    }

    private int calculateCoins(BigDecimal totalAmount) {
        return totalAmount
                .divide(BigDecimal.valueOf(COINS_PER_AMOUNT), RoundingMode.DOWN)
                .intValue();
    }

    public int getBalance(Long userId) {
        return superCoinTransactionRepository.getUserBalance(userId);
    }

    @Transactional
    public void reverseForRefund(Order order) {

        int coins = calculateCoins(order.getTotalAmount());

        SuperCoinTransaction reversal = SuperCoinTransaction.builder()
                .user(order.getUser())
                .amount(-coins) // debit
                .reason(SuperCoinReason.REFUND_REVERSAL)
                .referenceId(order.getId().toString())
                .build();

        superCoinTransactionRepository.save(reversal);
    }


}

// @Override
// public StripeResponse verifyPayment(String sessionId) {
// log.info("Stripe payment verified: sessionId={}", sessionId);
//
// Stripe.apiKey = secretKey;
// try {
// Session session = Session.retrieve(sessionId);
//
/// / extra safety
/// / if ("paid".equals(session.getPaymentStatus())
/// / && Session.Mode.PAYMENT.equals(session.getMode())) {
//
// // Stripe payment completed
// if ("paid".equals(session.getPaymentStatus())) {
//
// return StripeResponse.builder()
// .status("SUCCESS")
// .message("Payment verified successfully")
// .sessionId(sessionId)
// .build();
// }
//
// return StripeResponse.builder()
// .status("FAILED")
// .message("Payment not completed")
// .build();
//
// } catch (StripeException e) {
// return StripeResponse.builder()
// .status("ERROR")
// .message(e.getMessage())
// .build();
// }
// }

// private OrderResponse toOrderResponse(Order order, Session session) {
// // Build simplified OrderResponse from entity
// return OrderResponse.builder()
// .orderNumber(order.getOrderNumber())
// .status(order.getStatus().name())
// .message("Payment verified")
// .sessionId(order.getStripeSessionId())
// .itemsTotal(order.getItemsTotal())
// .shippingTotal(order.getShippingTotal())
// .taxTotal(order.getTaxTotal())
// .discountTotal(order.getDiscountTotal())
// .totalAmount(order.getTotalAmount())
// .currency(order.getCurrency())
// .paymentMethod(order.getPaymentMethod() == null ? null :
// order.getPaymentMethod().name())
// .stripeSessionId(order.getStripeSessionId())
// .stripePaymentIntentId(order.getStripePaymentIntentId())
// .paidAt(order.getPaidAt())
// .createdAt(order.getCreatedAt())
// .shippingAddress(order.getShippingAddress() == null ? null :
// com.projects.ecommerce.dto.AddressDto.from(order.getShippingAddress()))
// .items(order.getItems().stream().map(it ->
// com.projects.ecommerce.dto.response.OrderResponse.OrderItemResponse.builder()
// .productId(it.getProductId())
// .productName(it.getProductName())
// .variantId(it.getVariantId())
// .variantLabel(it.getVariantLabel())
// .sku(it.getSku())
// .unitPrice(it.getUnitPrice())
// .quantity(it.getQuantity())
// .lineTotal(it.getLineTotal())
// .build()).toList())
// .build();
// }

// @Override
// @Transactional
// public StripeResponse verifyPayment(String sessionId) {
// Stripe.apiKey = secretKey;
// try {
// Session session = Session.retrieve(sessionId);
//
// if (!"paid".equals(session.getPaymentStatus())) {
// return StripeResponse.builder()
// .status("FAILED")
// .message("Payment not completed")
// .sessionId(sessionId)
// .build();
// }
//
// // get orderId from metadata (preferred)
// String orderIdStr = session.getMetadata() != null ?
// session.getMetadata().get("orderId") : null;
// Order order = null;
// if (orderIdStr != null) {
// Long oid = Long.valueOf(orderIdStr);
// order = orderRepository.findById(oid).orElse(null);
// }
//
// // fallback: find by stripeSessionId
// if (order == null) {
// order = orderRepository.findByStripeSessionId(sessionId).orElse(null);
// }
//
// if (order == null) {
// // Could create order here from session metadata or return error
// return StripeResponse.builder()
// .status("ERROR")
// .message("Corresponding order not found")
// .sessionId(sessionId)
// .build();
// }
//
// // Idempotency: if already paid, return success
// if (order.getStatus() ==
// com.projects.ecommerce.enums.OrderStatusType.PAID
// || orderRepository.existsByStripePaymentIntentId(session.getPaymentIntent()))
// {
// return toOrderResponse(order, session);
// }
//
// // 1) Update order status + payment info
// order.setStatus(com.projects.ecommerce.enums.OrderStatusType.PAID);
// order.setStripePaymentIntentId(session.getPaymentIntent());
// order.setPaidAt(OffsetDateTime.now());
// // optionally set paid amounts from session (session.getAmountTotal() exists
// on Session API v)
/// / order = orderRepository.save(order);
//
// // 2) Reduce stock for each item
// for (OrderItem it : order.getItems()) {
// if (it.getVariantId() != null) {
// ProductVariant variant = variantRepository.findById(it.getVariantId())
// .orElseThrow(() -> new IllegalStateException("Variant not found: " +
// it.getVariantId()));
//
// int available = variant.getStock() == null ? 0 : variant.getStock();
// if (available < it.getQuantity()) {
// // handle low-stock situation — could throw, place backorder, or mark partial
// throw new IllegalStateException("Insufficient stock for variant: " +
// it.getVariantId());
// }
// variant.setStock(available - it.getQuantity());
// variantRepository.save(variant);
// } else {
// // If no variant id, you can attempt to reduce product-level stock if you
// have it
// }
// }
//
// // 3) Save final order
// order = orderRepository.save(order);
//
// return toOrderResponse(order, session);
//
// } catch (StripeException e) {
// log.error("Stripe verify error", e);
// return OrderResponse.builder()
// .status("ERROR")
// .message(e.getMessage())
// .sessionId(sessionId)
// .build();
// } catch (Exception ex) {
// log.error("Error in verifyPayment", ex);
// return OrderResponse.builder()
// .status("ERROR")
// .message(ex.getMessage())
// .sessionId(sessionId)
// .build();
// }
// }

// @Override
// public StripeResponse checkoutProducts(OrderRequest orderRequest) {
// Stripe.apiKey = secretKey;
//
// long unitAmount = orderRequest.getTotalAmount()
// .multiply(BigDecimal.valueOf(100))
// .longValue();
//
// String currency = orderRequest.getCurrency() == null
// ? "usd"
// : stripeRequest.getCurrency().toLowerCase();
//
// SessionCreateParams.LineItem.PriceData.ProductData productData =
// SessionCreateParams.LineItem.PriceData.ProductData.builder()
// .setName(stripeRequest.getName())
// .build();
//
// SessionCreateParams.LineItem.PriceData priceData =
// SessionCreateParams.LineItem.PriceData.builder()
// .setCurrency(currency)
// .setUnitAmount(unitAmount)
// .setProductData(productData)
// .build();
//
// SessionCreateParams.LineItem lineItem =
// SessionCreateParams.LineItem.builder()
// .setQuantity(stripeRequest.getQuantity().longValue())
// .setPriceData(priceData)
// .build();
//
// SessionCreateParams params = SessionCreateParams.builder()
// .setMode(SessionCreateParams.Mode.PAYMENT)
/// / You just write it as-is in the success URL: Stripe automatically replaces
// it at runtime.
// .setSuccessUrl("http://localhost:5500/payment-success?session_id={CHECKOUT_SESSION_ID}")
// .setCancelUrl("http://localhost:5500/payment-cancel")
// .addLineItem(lineItem)
// .build();
//
// try {
// Session session = Session.create(params);
//
// return StripeResponse.builder()
// .status("SUCCESS")
// .message("Payment session created")
// .sessionId(session.getId())
// .sessionUrl(session.getUrl())
// .build();
//
// } catch (StripeException e) {
// return StripeResponse.builder()
// .status("FAILED")
// .message(e.getMessage())
// .build();
// }
// }
