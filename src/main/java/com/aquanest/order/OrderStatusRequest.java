package com.aquanest.order;

import jakarta.validation.constraints.NotNull;

public record OrderStatusRequest(
        @NotNull
        OrderStatus status
) {}