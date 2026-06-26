package com.dozycoffee.product.domain;

import com.dozycoffee.core.domain.LongIdentifier;

public class CategoryId extends LongIdentifier {

    private CategoryId(long value) {
        super(value);
    }

    public static CategoryId of(long value) {
        return new CategoryId(value);
    }
}
