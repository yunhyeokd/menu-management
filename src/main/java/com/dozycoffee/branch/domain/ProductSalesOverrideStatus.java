package com.dozycoffee.domain.branch;

public enum ProductSalesOverrideStatus {
    SOLD_OUT,
    HIDDEN;

    public static ProductSalesOverrideStatus of(String status) {
        for (ProductSalesOverrideStatus productSalesOverrideStatus : values()) {
            if (productSalesOverrideStatus.name().equalsIgnoreCase(status)) {
                return productSalesOverrideStatus;
            }
        }
        throw new BranchException("Unknown ProductSalesOverride status " + status);
    }
}
