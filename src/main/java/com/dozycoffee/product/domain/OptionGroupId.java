package com.dozycoffee.product.domain;

import com.dozycoffee.core.domain.UuidIdentifier;

public class OptionGroupId extends UuidIdentifier {

    private OptionGroupId(String value) {
        super(value);
    }

    public static OptionGroupId of(String value) {
        return new OptionGroupId(value);
    }
}
