package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.application.dto.ProductRegisterCommand;
import com.dozycoffee.product.domain.AllergenInfo;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.ProductKind;

import java.util.HashSet;
import java.util.List;

public record ProductRegisterRequest(
        String productKind,
        BranchId branchId,
        String name,
        String description,
        String imageUrl,
        CategoryId categoryId,
        Integer price,
        Integer kcal,
        String allergenInfo,
        List<String> tags,
        List<OptionGroupLinkRequest> optionGroups
) {


    public ProductRegisterCommand toCommand() {
        return new ProductRegisterCommand(
                ProductKind.of(productKind),
                branchId,
                name,
                description,
                imageUrl,
                categoryId,
                price,
                kcal,
                AllergenInfo.of(allergenInfo),
                new HashSet<>(tags),
                optionGroups.stream().map(OptionGroupLinkRequest::toCommand).toList()
        );
    }

}
