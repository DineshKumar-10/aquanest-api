package com.aquanest.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(

        @NotBlank
        @Size(max = 100)
        String customerName,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Pattern(regexp = "^[6-9][0-9]{9}$")
        String phone,

        @NotBlank
        @Size(max = 300)
        String addressLine,

        @NotBlank
        @Size(max = 80)
        String city,

        @NotBlank
        @Size(max = 80)
        String state,

        @NotBlank
        @Pattern(regexp = "^[1-9][0-9]{5}$")
        String postalCode,

        @NotEmpty
        @Size(max = 20)
        List<@Valid OrderLineRequest> items

) {

    public record OrderLineRequest(

            @NotNull
            @Positive
            Long productId,

            @NotNull
            @Min(1)
            @Max(20)
            Integer quantity

    ) {}
}