package com.aquanest.product;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(
            AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    @GetMapping
    public Page<AdminProductResponse> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return adminProductService.getProducts(page, size);
    }

    @PostMapping
    public AdminProductResponse createProduct(
            @Valid @RequestBody AdminProductRequest request) {

        return adminProductService.createProduct(request);
    }

    @PutMapping("/{id}")
    public AdminProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductRequest request) {

        return adminProductService.updateProduct(id, request);
    }

    @PatchMapping("/{id}/stock")
    public AdminProductResponse updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {

        return adminProductService.updateStock(
                id, request.stock()
        );
    }

    @PatchMapping("/{id}/deactivate")
    public AdminProductResponse deactivateProduct(
            @PathVariable Long id) {

        return adminProductService.deactivateProduct(id);
    }
}