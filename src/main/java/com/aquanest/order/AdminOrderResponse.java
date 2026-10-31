package com.aquanest.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AdminOrderResponse(
        Long id,
        String orderNumber,
        String customerName,
        String email,
        String phone,
        String addressLine,
        String city,
        String state,
        String postalCode,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt,
        List<OrderLineResponse> items
) {

    public record OrderLineResponse(
            Long productId,
            String productName,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal lineTotal
    ) {}
}