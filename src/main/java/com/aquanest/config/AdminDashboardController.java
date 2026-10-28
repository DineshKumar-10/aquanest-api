package com.aquanest.config;

import com.aquanest.category.CategoryRepository;
import com.aquanest.product.ProductRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminDashboardController(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public record DashboardStats(
            long totalProducts,
            long activeProducts,
            long activeCategories,
            long unitsInStock
    ) {}

    @GetMapping
    public DashboardStats getDashboard() {
        return new DashboardStats(
                productRepository.count(),
                productRepository.countByActiveTrue(),
                categoryRepository.countByActiveTrue(),
                productRepository.totalActiveStock()
        );
    }
}