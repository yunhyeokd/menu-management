package com.dozycoffee.infrastructure.persistance.entity;

import java.time.Instant;

public record OptionGroupRow(
        String optionGroupId,
        String name,
        String description,
        Instant createdAt
) {}
