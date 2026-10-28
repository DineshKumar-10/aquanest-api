package com.aquanest.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AdminProductRequest(

        @NotBlank
        @Size(max = 120)
        String name,

        @NotNull
        @Positive
        Long categoryId,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,

        @NotNull
        @Min(0)
        @Max(100000)
        Integer stock,

        @NotBlank
        @Size(max = 2000)
        String description,

        @Size(max = 1000)
        String imageUrl

) {}