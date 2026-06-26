package com.dozycoffee.domain.product;

import com.dozycoffee.core.domain.LongIdentifier;

public class TagId extends LongIdentifier {

    private TagId(long value) {
        super(value);
    }

    public static TagId of(long value) {
        return new TagId(value);
    }
}
