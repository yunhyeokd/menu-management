package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.catalog.application.dto.TagData;

public record TagResponse(
        String tagId,
        String name
) {

    public static TagResponse from(TagData data) {
        return new TagResponse(data.id().getValue(), data.name());
    }
}
