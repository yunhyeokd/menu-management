package com.dozycoffee.product.application.dto;

import com.dozycoffee.product.domain.Tag;
import com.dozycoffee.product.domain.TagId;

public record TagData(
        TagId id,
        String name
) {
    public static TagData from(Tag tag) {
        return new TagData(tag.getId(), tag.getName());
    }
}
