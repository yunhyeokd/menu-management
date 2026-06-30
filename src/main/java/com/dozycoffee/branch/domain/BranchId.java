package com.dozycoffee.branch.domain;

import com.dozycoffee.core.domain.UuidIdentifier;

public class BranchId extends UuidIdentifier {

    private BranchId(String value) {
        super(value);
    }

    public static BranchId of(String value) {
        return new BranchId(value);
    }
}
