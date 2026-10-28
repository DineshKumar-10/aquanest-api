package com.aquanest.product;

import java.math.BigDecimal;

public record AdminProductResponse(
        Long id,
        String name,
        Long categoryId,
        String category,
        BigDecimal price,
        Integer stock,
        String description,
        String imageUrl,
        boolean active
) {}