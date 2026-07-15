package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.application.dto.ProductRegisterCommand;
import com.dozycoffee.catalog.domain.AllergenInfo;
import com.dozycoffee.catalog.domain.CategoryId;
import com.dozycoffee.catalog.domain.ProductKind;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.HashSet;
import java.util.List;

public record ProductRegisterRequest(
        @NotBlank String productKind,
        @NotBlank String branchId,
        @NotBlank String name,
        String description,
        String imageUrl,
        @NotBlank String categoryId,
        @NotNull @PositiveOrZero Integer price,
        @PositiveOrZero Integer kcal,
        @NotBlank String allergenInfo,
        @NotNull List<String> tags,
        @NotNull @Valid List<OptionGroupLinkRequest> optionGroups
) {


    public ProductRegisterCommand toCommand() {
        return new ProductRegisterCommand(
                ProductKind.of(productKind),
                BranchId.of(branchId),
                name,
                description,
                imageUrl,
                CategoryId.of(categoryId),
                price,
                kcal,
                AllergenInfo.of(allergenInfo),
                new HashSet<>(tags),
                optionGroups.stream().map(OptionGroupLinkRequest::toCommand).toList()
        );
    }

}
