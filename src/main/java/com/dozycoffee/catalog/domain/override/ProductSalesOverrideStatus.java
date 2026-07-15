package com.dozycoffee.catalog.domain.override;

import com.dozycoffee.catalog.domain.ProductException;

public enum ProductSalesOverrideStatus {
    SOLD_OUT,
    HIDDEN;

    public static ProductSalesOverrideStatus of(String status) {
        for (ProductSalesOverrideStatus productSalesOverrideStatus : values()) {
            if (productSalesOverrideStatus.name().equalsIgnoreCase(status)) {
                return productSalesOverrideStatus;
            }
        }
        throw new ProductException("Unknown ProductSalesOverride status " + status);
    }
}
