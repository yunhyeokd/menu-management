package com.dozycoffee.domain.branch;

public enum BranchStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    ;

    private final String value;
    BranchStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
