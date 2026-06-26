package com.dozycoffee.product.domain;

import com.dozycoffee.core.domain.LongIdentifier;

public class OptionGroupId extends LongIdentifier {

    private OptionGroupId(long value) {
        super(value);
    }

    public static OptionGroupId of(long value) {
        return new OptionGroupId(value);
    }
}
