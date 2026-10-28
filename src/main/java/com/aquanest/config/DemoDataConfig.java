//package com.aquanest.config;
//
//public class DemoDataConfig {
//}




package com.aquanest.config;

import java.math.BigDecimal;
import java.util.List;

import com.aquanest.category.Category;
import com.aquanest.category.CategoryRepository;
import com.aquanest.product.Product;
import com.aquanest.product.ProductRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataConfig {

    @Bean
    CommandLineRunner seedDemoData(
            CategoryRepository categories,
            ProductRepository products) {

        return args -> {
            // Do not duplicate demo products on later restarts.
            if (products.count() > 0) {
                return;
            }

            Category guppy = getOrCreateCategory(
                    categories,
                    "Guppy",
                    "Small, colourful ornamental fish."
            );

            Category betta = getOrCreateCategory(
                    categories,
                    "Betta",
                    "Ornamental fish known for flowing fins."
            );

            Category goldfish = getOrCreateCategory(
                    categories,
                    "Goldfish",
                    "Popular ornamental fish for suitable aquariums."
            );

            Category tetra = getOrCreateCategory(
                    categories,
                    "Tetra",
                    "Small schooling freshwater fish."
            );

            products.saveAll(List.of(
                    new Product(
                            "Blue Mosaic Guppy",
                            guppy,
                            new BigDecimal("120.00"),
                            12,
                            "A colourful guppy. Confirm water conditions and tank compatibility before purchase.",
                            "https://placehold.co/800x600/073B50/FFFFFF?text=Blue+Mosaic+Guppy"
                    ),
                    new Product(
                            "Red Betta",
                            betta,
                            new BigDecimal("350.00"),
                            8,
                            "A vibrant betta. Review tank setup and care requirements before purchase.",
                            "https://placehold.co/800x600/073B50/FFFFFF?text=Red+Betta"
                    ),
                    new Product(
                            "Fancy Goldfish",
                            goldfish,
                            new BigDecimal("450.00"),
                            5,
                            "Fancy goldfish needing adequate tank capacity and suitable filtration.",
                            "https://placehold.co/800x600/073B50/FFFFFF?text=Fancy+Goldfish"
                    ),
                    new Product(
                            "Red Guppy",
                            guppy,
                            new BigDecimal("100.00"),
                            15,
                            "A red-coloured guppy variety for a suitable freshwater aquarium.",
                            "https://placehold.co/800x600/073B50/FFFFFF?text=Red+Guppy"
                    ),
                    new Product(
                            "Neon Tetra",
                            tetra,
                            new BigDecimal("60.00"),
                            25,
                            "A small schooling fish. Check group size and compatibility requirements.",
                            "https://placehold.co/800x600/073B50/FFFFFF?text=Neon+Tetra"
                    ),
                    new Product(
                            "Koi Guppy",
                            guppy,
                            new BigDecimal("150.00"),
                            10,
                            "A patterned guppy variety for a well-maintained freshwater aquarium.",
                            "https://placehold.co/800x600/073B50/FFFFFF?text=Koi+Guppy"
                    )
            ));

            System.out.println(
                    "AquaNest demo catalogue created successfully."
            );
        };
    }

    private Category getOrCreateCategory(
            CategoryRepository repository,
            String name,
            String description) {

        return repository.findByNameIgnoreCase(name)
                .orElseGet(() ->
                        repository.save(new Category(name, description))
                );
    }
}