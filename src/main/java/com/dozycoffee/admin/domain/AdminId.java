package com.dozycoffee.admin.domain;

import com.dozycoffee.core.domain.LongIdentifier;

public class AdminId extends LongIdentifier {
    private AdminId(long value) {
        super(value);
    }

    public static AdminId of(long value) {
        return new AdminId(value);
    }

    public static AdminId of(String value) {
        return new AdminId(Long.parseLong(value));
    }

}
