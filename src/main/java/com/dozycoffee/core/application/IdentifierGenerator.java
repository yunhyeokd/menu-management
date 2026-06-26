package com.dozycoffee.application.common;

import com.dozycoffee.domain.common.Identifier;

public interface IdentifierGenerator<ID extends Identifier<?>> {
    ID generate();
}
