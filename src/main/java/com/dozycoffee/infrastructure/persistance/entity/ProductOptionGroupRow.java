package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductOptionGroup;

import java.time.Instant;

public record ProductOptionGroupRow(
        String productId,
        String optionGroupId,
        boolean isRequired,
        boolean allowMultiple,
        Instant createdAt
) {
    public ProductOptionGroup toProductOptionGroup() {
        return ProductOptionGroup.of(
                ProductId.of(productId),
                OptionGroupId.of(optionGroupId),
                isRequired,
                allowMultiple,
                createdAt
        );
    }
}
