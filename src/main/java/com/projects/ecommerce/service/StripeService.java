package com.projects.ecommerce.service;

import com.projects.ecommerce.dto.request.OrderRequest;
import com.projects.ecommerce.dto.response.StripeResponse;

public interface StripeService {
    StripeResponse checkoutProducts(OrderRequest stripeRequest);

    StripeResponse verifyPayment(String sessionId);
}
