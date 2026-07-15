package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.catalog.application.dto.ProductProfileUpdateCommand;
import com.dozycoffee.catalog.domain.AllergenInfo;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.domain.ProductId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

public record ProductModifyRequest(
        @NotBlank String name,
        String description,
        String imageUrl,
        @NotNull String categoryId,
        @NotNull @PositiveOrZero Integer price,
        @PositiveOrZero Integer kcal,
        @NotBlank String allergenInfo,
        @NotNull Set<String> tags
) {

    public ProductProfileUpdateCommand toCommand() {
        return new ProductProfileUpdateCommand(
                name,
                description,
                imageUrl,
                CategoryId.of(categoryId),
                price,
                kcal,
                AllergenInfo.of(allergenInfo),
                tags
        );
    }

}
