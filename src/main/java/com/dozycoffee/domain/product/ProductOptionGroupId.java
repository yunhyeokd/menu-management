package com.dozycoffee.domain.product;

import com.dozycoffee.domain.common.LongIdentifier;

public class ProductOptionGroupId extends LongIdentifier {
    private ProductOptionGroupId(long value) {
        super(value);
    }

    public static ProductOptionGroupId of(long value) {
        return new ProductOptionGroupId(value);
    }
}
