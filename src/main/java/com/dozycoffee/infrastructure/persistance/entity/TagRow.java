package com.dozycoffee.infrastructure.persistance.entity;

import com.dozycoffee.catalog.domain.Tag;
import com.dozycoffee.catalog.domain.TagId;

import java.time.Instant;

public record TagRow(
        String tagId,
        String name,
        Instant createdAt
) {
    public Tag toTag() {
        return Tag.of(TagId.of(tagId), name, createdAt);
    }
}
