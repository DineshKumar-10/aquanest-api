package com.aquanest.order;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Page<AdminOrderResponse> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return orderService.getAdminOrders(page, size);
    }

    @PatchMapping("/{id}/status")
    public AdminOrderResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest request) {

        return orderService.updateOrderStatus(
                id,
                request.status()
        );
    }
}