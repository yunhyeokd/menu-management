package com.dozycoffee.catalog.application.dto;

import com.dozycoffee.catalog.domain.Tag;
import com.dozycoffee.catalog.domain.TagId;

public record TagData(
        TagId id,
        String name
) {
    public static TagData from(Tag tag) {
        return new TagData(tag.getId(), tag.getName());
    }
}
