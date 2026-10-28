//package com.aquanest.product;
//
//public class ProductService {
//}









package com.aquanest.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            String search,
            String category,
            int page,
            int size) {

        if (page < 0 || size < 1 || size > 24) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be non-negative and size must be between 1 and 24."
            );
        }

        String searchValue = search == null ? "" : search.trim();
        String categoryValue = category == null ? "" : category.trim();

        if (searchValue.length() > 100 || categoryValue.length() > 80) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search or category value is too long."
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "name")
        );

        return productRepository
                .searchActiveProducts(searchValue, categoryValue, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = productRepository
                .findByIdAndActiveTrue(id)
                .filter(item -> item.getCategory().isActive())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found."
                ));

        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getName(),
                product.getPrice(),
                product.getDescription(),
                product.getImageUrl(),
                product.getStock() > 0
        );
    }
}