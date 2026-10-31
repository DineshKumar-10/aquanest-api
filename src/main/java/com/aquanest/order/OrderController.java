package com.aquanest.order;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderConfirmationResponse placeOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return orderService.placeOrder(request);
    }
}