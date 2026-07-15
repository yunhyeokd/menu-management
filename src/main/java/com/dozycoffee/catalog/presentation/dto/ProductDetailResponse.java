package com.dozycoffee.catalog.presentation.dto;

import com.dozycoffee.catalog.application.dto.LinkedOptionGroupData;
import com.dozycoffee.catalog.application.dto.OptionItemData;
import com.dozycoffee.catalog.application.dto.ProductDetailResult;
import com.dozycoffee.catalog.application.dto.TagData;

import java.time.Instant;
import java.util.List;

public record ProductDetailResponse(
        String productId,
        String name,
        String description,
        String imageUrl,
        CategorySummary category,
        int price,
        Integer kcal,
        String allergenInfo,
        String kind,
        String branchId,
        String status,
        List<String> tags,
        List<OptionGroupSummary> optionGroups,
        Instant createdAt
) {

    public static ProductDetailResponse from(ProductDetailResult result) {
        return new ProductDetailResponse(
                result.id().getValue(),
                result.name(),
                result.description(),
                result.imageUrl(),
                result.category() != null ? new CategorySummary(result.category().id().getValue(), result.category().name()) : null,
                result.price(),
                result.kcal(),
                result.allergenInfo() != null ? result.allergenInfo().toString() : null,
                result.kind().name(),
                result.branchId() != null ? result.branchId().getValue() : null,
                result.status().name(),
                result.tags().stream().map(TagData::name).toList(),
                result.optionGroups().stream().map(OptionGroupSummary::from).toList(),
                result.createdAt()
        );
    }

    public record CategorySummary(String categoryId, String name) {
    }

    public record OptionGroupSummary(
            String optionGroupId,
            String name,
            String description,
            boolean isRequired,
            boolean allowMultiple,
            List<OptionItemSummary> items
    ) {
        public static OptionGroupSummary from(LinkedOptionGroupData data) {
            return new OptionGroupSummary(
                    data.optionGroupId().getValue(),
                    data.name(),
                    data.description(),
                    data.isRequired(),
                    data.allowMultiple(),
                    data.items().stream().map(OptionItemSummary::from).toList()
            );
        }
    }

    public record OptionItemSummary(String name, String description, int price) {
        public static OptionItemSummary from(OptionItemData data) {
            return new OptionItemSummary(data.name(), data.description(), data.price());
        }
    }
}
