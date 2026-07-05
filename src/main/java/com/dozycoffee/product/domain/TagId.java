package com.dozycoffee.product.domain;

import com.dozycoffee.core.id.UuidIdentifier;

public class TagId extends UuidIdentifier {

    private TagId(String value) {
        super(value);
    }

    public static TagId of(String value) {
        return new TagId(value);
    }
}
