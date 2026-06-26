package com.dozycoffee.domain.branch;

import com.dozycoffee.core.domain.LongIdentifier;

public class BranchId extends LongIdentifier {

    public BranchId(long id) {
        super(id);
    }

    public static BranchId of(long value) {
        return new BranchId(value);
    }

    public static BranchId of(String value) {
        return new BranchId(Long.parseLong(value));
    }
}
