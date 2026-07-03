package com.dozycoffee.product.presentation.dto;

import com.dozycoffee.product.application.dto.LinkedOptionGroupData;
import com.dozycoffee.product.application.dto.OptionItemData;
import com.dozycoffee.product.application.dto.ProductDetailResult;
import com.dozycoffee.product.application.dto.TagData;

import java.time.Instant;
import java.util.List;

public record ProductDetailResponse(
        String product_id,
        String name,
        String description,
        String image_url,
        CategorySummary category,
        int price,
        Integer kcal,
        String allergen_info,
        String kind,
        String branch_id,
        String status,
        List<String> tags,
        List<OptionGroupSummary> option_groups,
        Instant created_at
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

    public record CategorySummary(String category_id, String name) {
    }

    public record OptionGroupSummary(
            String option_group_id,
            String name,
            String description,
            boolean is_required,
            boolean allow_multiple,
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
