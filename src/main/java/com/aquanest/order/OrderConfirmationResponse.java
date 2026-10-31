package com.aquanest.order;

import java.math.BigDecimal;

public record OrderConfirmationResponse(
        String orderNumber,
        BigDecimal totalAmount,
        OrderStatus status,
        String message
) {}