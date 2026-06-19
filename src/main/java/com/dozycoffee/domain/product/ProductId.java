package com.dozycoffee.domain.product;

import com.dozycoffee.domain.common.LongIdentifier;

public class ProductId extends LongIdentifier {

    private ProductId(long value) {
        super(value);
    }

    public static ProductId of(long value) {
        return new ProductId(value);
    }
}
