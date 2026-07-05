package com.dozycoffee.infrastructure.generator;

import com.dozycoffee.core.id.Identifier;
import com.dozycoffee.core.id.IdentifierGenerator;
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
