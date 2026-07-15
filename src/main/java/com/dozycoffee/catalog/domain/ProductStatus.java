package com.dozycoffee.catalog.domain;

public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    ;

    public static ProductStatus of(String code) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new ProductException("Unknown product status: " + code);
    }
}