package com.dozycoffee.core.application;

import com.dozycoffee.core.domain.Identifier;

public interface IdentifierGenerator<ID extends Identifier<?>> {
    ID generate();
}
