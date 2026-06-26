package com.dozycoffee.domain.product;

public enum ProductKind {
    COMMON,
    BRANCH_EXCLUSIVE,
    ;

    public static ProductKind of(String code) {
        for (ProductKind kind : ProductKind.values()) {
            if (kind.name().equalsIgnoreCase(code)) {
                return kind;
            }
        }
        throw new ProductException("Unknown product kind: " + code);
    }
}