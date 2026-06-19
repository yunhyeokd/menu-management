package com.dozycoffee.domain.product;

import com.dozycoffee.domain.common.LongIdentifier;

public class OptionItemId extends LongIdentifier {

    private OptionItemId(long value) {
        super(value);
    }

    public static OptionItemId of(long value) {
        return new OptionItemId(value);
    }
}
