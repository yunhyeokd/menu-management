package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.ProductProfileUpdateCommand;
import com.dozycoffee.product.domain.AllergenInfo;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.ProductId;

import java.util.Set;

public record ProductModifyRequest(
        String name,
        String description,
        String imageUrl,
        CategoryId categoryId,
        Integer price,
        Integer kcal,
        String allergenInfo,
        Set<String> tags
) {

    public ProductProfileUpdateCommand toCommand() {
        return new ProductProfileUpdateCommand(
                name,
                description,
                imageUrl,
                categoryId,
                price,
                kcal,
                AllergenInfo.of(allergenInfo),
                tags
        );
    }

}
