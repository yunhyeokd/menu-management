package com.dozycoffee.domain.admin;

public enum AdminStatus {
    PENDING("pending"),
    ACTIVE("active"),
    INACTIVE("inactive");
    private final String value;
    AdminStatus(String value) {
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
