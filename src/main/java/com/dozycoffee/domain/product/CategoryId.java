package com.dozycoffee.domain.product;

import com.dozycoffee.domain.common.LongIdentifier;

public class CategoryId extends LongIdentifier {

    private CategoryId(long value) {
        super(value);
    }

    public static CategoryId of(long value) {
        return new CategoryId(value);
    }
}
