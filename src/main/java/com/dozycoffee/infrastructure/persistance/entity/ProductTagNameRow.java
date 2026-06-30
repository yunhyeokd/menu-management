package com.dozycoffee.infrastructure.persistance.entity;

public record ProductTagNameRow(
        String productId,
        String tagId,
        String tagName
) {}
