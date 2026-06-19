package com.dozycoffee.domain.admin;

import com.dozycoffee.domain.common.LongIdentifier;

public class AdminId extends LongIdentifier {
    private AdminId(long value) {
        super(value);
    }

    public static AdminId of(long value) {
        return new AdminId(value);
    }

}
