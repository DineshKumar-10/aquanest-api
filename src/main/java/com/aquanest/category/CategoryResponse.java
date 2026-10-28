//package com.aquanest.category;
//
//public class CategoryResponse {
//}







package com.aquanest.category;

public record CategoryResponse(
        Long id,
        String name,
        String description
) {
}