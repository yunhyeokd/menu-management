package com.dozycoffee.domain.branch;

import com.dozycoffee.domain.common.LongIdentifier;

public class BranchId extends LongIdentifier {

    public BranchId(long id) {
        super(id);
    }

    public static BranchId of(long value) {
        return new BranchId(value);
    }
}
