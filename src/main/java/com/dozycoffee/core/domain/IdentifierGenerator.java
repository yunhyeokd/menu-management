package com.dozycoffee.core.domain;

public interface IdentifierGenerator<ID extends Identifier<?>> {
    ID generate();
}
