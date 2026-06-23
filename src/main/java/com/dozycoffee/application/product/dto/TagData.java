package com.dozycoffee.application.product.dto;

import com.dozycoffee.domain.product.Tag;
import com.dozycoffee.domain.product.TagId;

public record TagData(
        TagId id,
        String name
) {
    public static TagData from(Tag tag) {
        return new TagData(tag.getId(), tag.getName());
    }
}
