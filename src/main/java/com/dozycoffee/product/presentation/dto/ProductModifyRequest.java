package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.ProductProfileUpdateCommand;
import com.dozycoffee.product.domain.AllergenInfo;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.ProductId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

public record ProductModifyRequest(
        @NotBlank String name,
        String description,
        String imageUrl,
        @NotNull CategoryId categoryId,
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
                categoryId,
                price,
                kcal,
                AllergenInfo.of(allergenInfo),
                tags
        );
    }

}
