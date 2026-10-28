




package com.aquanest.product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String category,
        BigDecimal price,
        String description,
        String imageUrl,
        boolean inStock
)
{
}