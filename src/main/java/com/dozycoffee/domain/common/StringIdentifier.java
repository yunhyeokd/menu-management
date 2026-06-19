package com.dozycoffee.domain.common;

public abstract class StringIdentifier implements Identifier<String> {

    private final String value;

    protected StringIdentifier(String value) {
        validate(value);
        this.value = value;
    }

    protected void validate(String value) {
        if (value == null) throw new DomainException("identifier value must not be null");
    }

    @Override
    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value.equals(((StringIdentifier) o).value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }

}
