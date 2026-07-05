package com.dozycoffee.core.id;

import java.util.Objects;

public abstract class UuidIdentifier implements Identifier<String> {

    private final String value;

    protected UuidIdentifier(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return getValue().equals(((UuidIdentifier) o).getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return value;
    }
}
