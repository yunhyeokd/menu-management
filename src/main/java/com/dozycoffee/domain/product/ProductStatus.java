package com.dozycoffee.domain.product;

public enum ProductStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    ;

    private final String value;
    ProductStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProductStatus of(String value) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }


}
