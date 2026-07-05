package com.dozycoffee.core.id;

import java.util.Objects;

public abstract class LongIdentifier implements Identifier<Long> {

    private final long value;

    protected LongIdentifier(long value) {
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return getValue().equals(((LongIdentifier) o).getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
