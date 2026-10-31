package com.aquanest.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.aquanest.product.Product;
import com.aquanest.product.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(
            CustomerOrderRepository orderRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates a customer order request.
     * Prices are always obtained from the database.
     * Stock is checked but not deducted at this stage.
     */
//    @Transactional
//    public OrderConfirmationResponse placeOrder(
//            CreateOrderRequest request) {
//
//        Set<Long> productIds = new HashSet<>();
//
//        for (CreateOrderRequest.OrderLineRequest line : request.items()) {
//            if (!productIds.add(line.productId())) {
//                throw new ResponseStatusException(
//                        HttpStatus.BAD_REQUEST,
//                        "A product cannot appear more than once in the cart."
//                );
//            }
//        }
//
//        List<Product> products =
//                productRepository.findAllById(productIds);
//
//        if (products.size() != productIds.size()) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND,
//                    "One or more selected products do not exist."
//            );
//        }
//
//        Map<Long, Product> productMap = new HashMap<>();
//
//        for (Product product : products) {
//            productMap.put(product.getId(), product);
//        }
//
//        BigDecimal total = BigDecimal.ZERO;
//
//        CustomerOrder order = new CustomerOrder(
//                generateOrderNumber(),
//                request.customerName().trim(),
//                request.email().trim().toLowerCase(Locale.ROOT),
//                request.phone().trim(),
//                request.addressLine().trim(),
//                request.city().trim(),
//                request.state().trim(),
//                request.postalCode().trim(),
//                total
//        );
//
//        for (CreateOrderRequest.OrderLineRequest line : request.items()) {
//
//            Product product = productMap.get(line.productId());
//
//            if (!product.isActive() || !product.getCategory().isActive()) {
//                throw new ResponseStatusException(
//                        HttpStatus.CONFLICT,
//                        product.getName() + " is not currently available."
//                );
//            }
//
//            if (product.getStock() < line.quantity()) {
//                throw new ResponseStatusException(
//                        HttpStatus.CONFLICT,
//                        "Insufficient stock for " + product.getName() + "."
//                );
//            }
//
//            BigDecimal unitPrice = product.getPrice();
//
//            BigDecimal lineTotal = unitPrice.multiply(
//                    BigDecimal.valueOf(line.quantity())
//            );
//
//            total = total.add(lineTotal);
//
//            order.addItem(new OrderItem(
//                    product,
//                    product.getName(),
//                    unitPrice,
//                    line.quantity(),
//                    lineTotal
//            ));
//        }

        // Set the server-calculated total.
//        CustomerOrder orderWithTotal = new CustomerOrder(
//                order.getOrderNumber(),
//                order.getCustomerName(),
//                order.getEmail(),
//                order.getPhone(),
//                order.getAddressLine(),
//                order.getCity(),
//                order.getState(),
//                order.getPostalCode(),
//                total
//        );
//
//        // Copy the original items onto the final order.
//        for (OrderItem item : order.getItems()) {
//            orderWithTotal.addItem(item);
//        }
//
//        CustomerOrder saved = orderRepository.save(orderWithTotal);
//
//        return new OrderConfirmationResponse(
//                saved.getOrderNumber(),
//                saved.getTotalAmount(),
//                saved.getStatus(),
//                "Your order request was received and is awaiting confirmation."
//        );
//    }


    @Transactional
    public OrderConfirmationResponse placeOrder(
            CreateOrderRequest request) {

        Set<Long> productIds = new HashSet<>();

        for (CreateOrderRequest.OrderLineRequest line : request.items()) {

            if (!productIds.add(line.productId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A product cannot appear more than once in the cart."
                );
            }
        }

        List<Product> products =
                productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "One or more selected products do not exist."
            );
        }

        Map<Long, Product> productMap = new HashMap<>();

        for (Product product : products) {
            productMap.put(product.getId(), product);
        }

        BigDecimal total = BigDecimal.ZERO;

        List<OrderItem> orderItems = new java.util.ArrayList<>();

        for (CreateOrderRequest.OrderLineRequest line : request.items()) {

            Product product = productMap.get(line.productId());

            if (!product.isActive() ||
                    !product.getCategory().isActive()) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        product.getName() + " is not currently available."
                );
            }

            if (product.getStock() < line.quantity()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Insufficient stock for " + product.getName() + "."
                );
            }

            BigDecimal unitPrice = product.getPrice();

            BigDecimal lineTotal = unitPrice.multiply(
                    BigDecimal.valueOf(line.quantity())
            );

            total = total.add(lineTotal);

            orderItems.add(new OrderItem(
                    product,
                    product.getName(),
                    unitPrice,
                    line.quantity(),
                    lineTotal
            ));
        }

        CustomerOrder order = new CustomerOrder(
                generateOrderNumber(),
                request.customerName().trim(),
                request.email().trim().toLowerCase(Locale.ROOT),
                request.phone().trim(),
                request.addressLine().trim(),
                request.city().trim(),
                request.state().trim(),
                request.postalCode().trim(),
                total
        );

        for (OrderItem item : orderItems) {
            order.addItem(item);
        }

        CustomerOrder saved = orderRepository.save(order);

        return new OrderConfirmationResponse(
                saved.getOrderNumber(),
                saved.getTotalAmount(),
                saved.getStatus(),
                "Your order request was received and is awaiting confirmation."
        );
    }



    @Transactional(readOnly = true)
    public Page<AdminOrderResponse> getAdminOrders(
            int page,
            int size) {

        if (page < 0 || size < 1 || size > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid page or page size."
            );
        }

        return orderRepository.findAll(
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        ).map(this::toAdminResponse);
    }

    @Transactional
    public AdminOrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus requestedStatus) {

        CustomerOrder order = orderRepository
                .findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found."
                ));

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == requestedStatus) {
            return toAdminResponse(order);
        }

        if (currentStatus == OrderStatus.PENDING_CONFIRMATION) {

            if (requestedStatus == OrderStatus.CONFIRMED) {
                confirmOrder(order);
            } else if (requestedStatus == OrderStatus.REJECTED) {
                order.setStatus(OrderStatus.REJECTED);
            } else {
                throw invalidTransition();
            }

        } else if (currentStatus == OrderStatus.CONFIRMED) {

            if (requestedStatus == OrderStatus.COMPLETED) {
                order.setStatus(OrderStatus.COMPLETED);

            } else if (requestedStatus == OrderStatus.CANCELLED) {
                cancelConfirmedOrder(order);

            } else {
                throw invalidTransition();
            }

        } else {
            throw invalidTransition();
        }

        return toAdminResponse(order);
    }

    /**
     * Rechecks and deducts inventory under database locks.
     */
    private void confirmOrder(CustomerOrder order) {

        List<Product> lockedProducts = lockOrderProducts(order);

        Map<Long, Product> productMap = new HashMap<>();

        for (Product product : lockedProducts) {
            productMap.put(product.getId(), product);
        }

        // Validate every item before changing any stock.
        for (OrderItem item : order.getItems()) {

            Product product = productMap.get(item.getProduct().getId());

            if (product == null ||
                    !product.isActive() ||
                    !product.getCategory().isActive()) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "A product in this order is no longer available."
                );
            }

            if (product.getStock() < item.getQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Insufficient current stock for " +
                                item.getProductName() + "."
                );
            }
        }

        // Deduct only after all products pass validation.
        for (OrderItem item : order.getItems()) {

            Product product = productMap.get(item.getProduct().getId());

            product.setStock(
                    product.getStock() - item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CONFIRMED);
    }

    /**
     * Restores stock if a confirmed order is cancelled.
     */
    private void cancelConfirmedOrder(CustomerOrder order) {

        List<Product> lockedProducts = lockOrderProducts(order);

        Map<Long, Product> productMap = new HashMap<>();

        for (Product product : lockedProducts) {
            productMap.put(product.getId(), product);
        }

        for (OrderItem item : order.getItems()) {

            Product product = productMap.get(item.getProduct().getId());

            if (product == null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Unable to restore inventory for this order."
                );
            }

            product.setStock(
                    product.getStock() + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
    }

    private List<Product> lockOrderProducts(CustomerOrder order) {

        List<Long> productIds = order.getItems()
                .stream()
                .map(item -> item.getProduct().getId())
                .distinct()
                .sorted()
                .toList();

        return productRepository.findAllByIdsForUpdate(productIds);
    }

    private AdminOrderResponse toAdminResponse(CustomerOrder order) {

        List<AdminOrderResponse.OrderLineResponse> lines =
                order.getItems().stream()
                        .map(item ->
                                new AdminOrderResponse.OrderLineResponse(
                                        item.getProduct().getId(),
                                        item.getProductName(),
                                        item.getUnitPrice(),
                                        item.getQuantity(),
                                        item.getLineTotal()
                                )
                        )
                        .toList();

        return new AdminOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getEmail(),
                order.getPhone(),
                order.getAddressLine(),
                order.getCity(),
                order.getState(),
                order.getPostalCode(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                lines
        );
    }

    private String generateOrderNumber() {
        return "AN-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase(Locale.ROOT);
    }

    private ResponseStatusException invalidTransition() {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This order status transition is not allowed."
        );
    }
}