package com.dozycoffee.product.domain;

import com.dozycoffee.core.domain.LongIdentifier;

public class ProductId extends LongIdentifier {

    private ProductId(long value) {
        super(value);
    }

    public static ProductId of(long value) {
        return new ProductId(value);
    }
}
