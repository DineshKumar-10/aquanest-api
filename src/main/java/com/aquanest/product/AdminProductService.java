package com.aquanest.product;

import com.aquanest.category.Category;
import com.aquanest.category.CategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminProductResponse> getProducts(
            int page, int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid page or page size."
            );
        }

        return productRepository.findAll(
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "id")
                )
        ).map(this::toResponse);
    }

    @Transactional
    public AdminProductResponse createProduct(
            AdminProductRequest request) {

        Category category = getActiveCategory(request.categoryId());

        Product product = new Product(
                request.name().trim(),
                category,
                request.price(),
                request.stock(),
                request.description().trim(),
                normalizeImageUrl(request.imageUrl())
        );

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public AdminProductResponse updateProduct(
            Long id,
            AdminProductRequest request) {

        Product product = findProduct(id);
        Category category = getActiveCategory(request.categoryId());

        product.setName(request.name().trim());
        product.setCategory(category);
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setDescription(request.description().trim());
        product.setImageUrl(normalizeImageUrl(request.imageUrl()));

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public AdminProductResponse updateStock(
            Long id, int stock) {

        if (stock < 0 || stock > 100000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Stock must be between 0 and 100000."
            );
        }

        Product product = findProduct(id);
        product.setStock(stock);

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public AdminProductResponse deactivateProduct(Long id) {
        Product product = findProduct(id);

        // Soft deactivation preserves order references and history.
        product.setActive(false);

        return toResponse(productRepository.save(product));
    }

//    private Product findProduct(Long id) {
//        return productRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND,
//                        "Product not found."
//                ));
//    }


    private Product findProduct(Long id) {
        return productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found."
                ));
    }

    private Category getActiveCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .filter(Category::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The selected category is unavailable."
                ));
    }

    private String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        return imageUrl.trim();
    }

    private AdminProductResponse toResponse(Product product) {
        return new AdminProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getImageUrl(),
                product.isActive()
        );
    }
}