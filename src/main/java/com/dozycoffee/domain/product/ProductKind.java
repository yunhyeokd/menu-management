package com.dozycoffee.domain.product;

public enum ProductKind {
    COMMON("common"),
    BRANCH_EXCLUSIVE("branch_exclusive"),
    ;

    private final String value;
    ProductKind(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProductKind of(String value) {
        for (ProductKind kind : ProductKind.values()) {
            if (kind.value.equals(value)) {
                return kind;
            }
        }
        return null;
    }
}
