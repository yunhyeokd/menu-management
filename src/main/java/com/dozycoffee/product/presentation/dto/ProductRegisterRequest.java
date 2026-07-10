package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.application.dto.ProductRegisterCommand;
import com.dozycoffee.product.domain.AllergenInfo;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.ProductKind;
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
