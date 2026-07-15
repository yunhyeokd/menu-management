package com.dozycoffee.catalog.domain;

import com.dozycoffee.core.id.UuidIdentifier;

public class ProductId extends UuidIdentifier {

    private ProductId(String value) {
        super(value);
    }

    public static ProductId of(String value) {
        return new ProductId(value);
    }
}
