package com.aquanest.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockUpdateRequest(
        @NotNull
        @Min(0)
        @Max(100000)
        Integer stock
) {}