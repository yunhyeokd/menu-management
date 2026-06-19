package com.dozycoffee.domain.product;

import com.dozycoffee.domain.common.LongIdentifier;

public class OptionGroupId extends LongIdentifier {

    private OptionGroupId(long value) {
        super(value);
    }

    public static OptionGroupId of(long value) {
        return new OptionGroupId(value);
    }
}
