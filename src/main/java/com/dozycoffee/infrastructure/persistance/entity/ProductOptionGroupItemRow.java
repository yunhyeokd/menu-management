package com.dozycoffee.infrastructure.persistance.entity;

import java.time.Instant;

public record ProductOptionGroupItemRow(
        String productId,
        String optionGroupId,
        String groupName,
        String groupDescription,
        boolean isRequired,
        boolean allowMultiple,
        String itemName,
        String itemDescription,
        int itemPrice,
        Instant itemCreatedAt
) {}
