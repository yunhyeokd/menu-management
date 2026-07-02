package com.dozycoffee.infrastructure.web;

import org.springframework.core.convert.converter.Converter;

import java.util.function.Function;

public class IdConverterFactory<ID> implements Converter<String, ID> {

    private final Function<String, ID> factory;

    public IdConverterFactory(Function<String, ID> factory) {
        this.factory = factory;
    }

    @Override
    public ID convert(String source) {
        return factory.apply(source);
    }
}
