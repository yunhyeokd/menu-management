package com.dozycoffee.domain.admin;

public enum AdminRole {
    SYSTEM("system"),
    STAFF("staff"),
    ;

    private final String value;
    AdminRole(String value) {
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
