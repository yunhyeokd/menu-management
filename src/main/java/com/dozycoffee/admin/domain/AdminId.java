package com.dozycoffee.admin.domain;

import com.dozycoffee.core.domain.UuidIdentifier;

public class AdminId extends UuidIdentifier {
    private AdminId(String value) {
        super(value);
    }

    public static AdminId of(String value) {
        return new AdminId(value);
    }
}
