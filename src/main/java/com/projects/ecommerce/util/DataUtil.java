package com.projects.ecommerce.util;


import com.projects.ecommerce.dto.request.OrderRequest;
import com.projects.ecommerce.entity.Address;
import com.projects.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class DataUtil {



    /**
     * Server-side shipping calculation example.
     * Replace with real ShippingService logic (zones, weight, carrier API).
     */
    public BigDecimal calculateShippingTotal(OrderRequest req, List<OrderItem> items, Address snapshot) {
        // simple example: free shipping over 1000, else flat 50
        BigDecimal itemsTotal = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal freeThreshold = new BigDecimal("1000.00");
        if (itemsTotal.compareTo(freeThreshold) >= 0)
            return BigDecimal.ZERO;
        return new BigDecimal("50.00");
    }

    /**
     * Server-side tax calculation example.
     * Replace with real TaxService (GST/VAT rules).
     */
    public BigDecimal calculateTaxTotal(OrderRequest req, List<OrderItem> items, Address snapshot) {
        BigDecimal itemsTotal = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // simple example: 18% tax
        BigDecimal taxRate = new BigDecimal("0.18");
        return itemsTotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Discount calculation example (coupon-based).
     * If you have no coupon logic, return ZERO.
     */
    public BigDecimal calculateDiscountTotal(OrderRequest req, List<OrderItem> items) {
        // example: if front-end sent a couponCode (you'd need to add field), validate &
        // compute
        return BigDecimal.ZERO;
    }
}
