package com.dozycoffee.core.id;

public interface IdentifierGenerator<ID extends Identifier<?>> {
    ID generate();
}
