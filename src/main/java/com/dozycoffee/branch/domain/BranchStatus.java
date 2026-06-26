package com.dozycoffee.branch.domain;



public enum BranchStatus {
    ACTIVE,
    INACTIVE,
    ;

    public static BranchStatus of(String code) {
        for (BranchStatus status : BranchStatus.values()) {
            if (status.name().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new BranchException("Unknown branch status: " + code);
    }
}