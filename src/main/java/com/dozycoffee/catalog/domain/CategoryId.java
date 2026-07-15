package com.dozycoffee.catalog.domain;

import com.dozycoffee.core.id.UuidIdentifier;

public class CategoryId extends UuidIdentifier {

    private CategoryId(String value) {
        super(value);
    }

    public static CategoryId of(String value) {
        return new CategoryId(value);
    }
}
