package com.dozycoffee.product.domain;

import com.dozycoffee.core.domain.UuidIdentifier;

public class ProductId extends UuidIdentifier {

    private ProductId(String value) {
        super(value);
    }

    public static ProductId of(String value) {
        return new ProductId(value);
    }
}
