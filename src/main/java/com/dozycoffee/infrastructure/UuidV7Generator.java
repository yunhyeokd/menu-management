package com.dozycoffee.infrastructure;

import com.dozycoffee.core.domain.Identifier;
import com.dozycoffee.core.domain.IdentifierGenerator;
import com.fasterxml.uuid.Generators;

import java.util.function.Function;

public class UuidV7Generator<ID extends Identifier<String>> implements IdentifierGenerator<ID> {

    private final Function<String, ID> factory;

    public UuidV7Generator(Function<String, ID> factory) {
        this.factory = factory;
    }

    @Override
    public ID generate() {
        return factory.apply(Generators.timeBasedEpochGenerator().generate().toString());
    }
}
