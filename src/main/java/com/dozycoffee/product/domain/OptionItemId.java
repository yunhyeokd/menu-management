package com.dozycoffee.product.domain;

import com.dozycoffee.core.domain.LongIdentifier;

public class OptionItemId extends LongIdentifier {

    private OptionItemId(long value) {
        super(value);
    }

    public static OptionItemId of(long value) {
        return new OptionItemId(value);
    }
}
