package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.ProductOptionGroupItemRow;
import com.dozycoffee.infrastructure.persistance.entity.ProductTagNameRow;
import com.dozycoffee.infrastructure.persistance.entity.ProductWithCategoryRow;
import com.dozycoffee.infrastructure.persistance.mapper.ProductQueryMapper;
import com.dozycoffee.catalog.application.dto.*;
import com.dozycoffee.catalog.application.repository.ProductQueryRepository;
import com.dozycoffee.catalog.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyBatisProductQueryRepository implements ProductQueryRepository {

    private final ProductQueryMapper productQueryMapper;

    @Override
    public List<ProductSummaryResult> findByFilter(ProductFilterQuery filter) throws RepositoryException {
        List<ProductWithCategoryRow> products = productQueryMapper.findByFilter(filter);
        if (products.isEmpty()) return List.of();

        List<String> productIds = products.stream().map(ProductWithCategoryRow::productId).toList();
        Map<String, List<TagData>> tagsByProductId = groupTagsByProductId(productIds);

        return products.stream()
                .map(row -> toProductSummaryResult(row, tagsByProductId.getOrDefault(row.productId(), List.of())))
                .toList();
    }

    @Override
    public Optional<ProductDetailResult> findDetailById(ProductId productId) throws RepositoryException {
        Optional<ProductWithCategoryRow> row = productQueryMapper.findByProductId(productId.getValue());
        if (row.isEmpty()) return Optional.empty();

        List<String> productIds = List.of(productId.getValue());
        List<TagData> tags = groupTagsByProductId(productIds).getOrDefault(productId.getValue(), List.of());
        List<LinkedOptionGroupData> optionGroups = groupOptionGroupsByProductId(productIds).getOrDefault(productId.getValue(), List.of());

        return Optional.of(toProductDetailResult(row.get(), tags, optionGroups));
    }

    @Override
    public Map<ProductId, List<TagData>> findTagsByProductIds(List<ProductId> productIds) throws RepositoryException {
        if (productIds.isEmpty()) return Map.of();
        List<String> ids = productIds.stream().map(ProductId::getValue).toList();
        return groupTagsByProductId(ids).entrySet().stream()
                .collect(Collectors.toMap(e -> ProductId.of(e.getKey()), Map.Entry::getValue));
    }

    // 상품:태그, 상품:옵션그룹:옵션아이템은 각각 1:N, 1:N:N 관계라서 하나의 SQL로 조인하면
    // 상품 row가 태그/옵션 개수만큼 fan-out되어 중복된다. 그래서 정규화된 별도 쿼리로 조회한 뒤
    // productId(옵션은 productId+optionGroupId) 기준으로 메모리에서 다시 묶어 상품 단위로 복원한다.
    private Map<String, List<LinkedOptionGroupData>> groupOptionGroupsByProductId(List<String> productIds) {
        List<ProductOptionGroupItemRow> rows = productQueryMapper.findOptionGroupsByProductIds(productIds);

        Map<String, Map<String, List<OptionItemData>>> itemsByProductAndGroup = rows.stream()
                .collect(Collectors.groupingBy(
                        ProductOptionGroupItemRow::productId,
                        Collectors.groupingBy(
                                ProductOptionGroupItemRow::optionGroupId,
                                Collectors.mapping(this::toOptionItemData, Collectors.toList())
                        )
                ));

        Map<String, List<LinkedOptionGroupData>> optionGroupsByProductId = new LinkedHashMap<>();
        rows.stream()
                .collect(Collectors.groupingBy(row -> row.productId() + ":" + row.optionGroupId()))
                .forEach((key, groupRows) -> {
                    ProductOptionGroupItemRow first = groupRows.get(0);
                    String productId = first.productId();
                    List<OptionItemData> items = itemsByProductAndGroup
                            .getOrDefault(productId, Map.of())
                            .getOrDefault(first.optionGroupId(), List.of());
                    optionGroupsByProductId
                            .computeIfAbsent(productId, k -> new ArrayList<>())
                            .add(toLinkedOptionGroupData(first, items));
                });
        return optionGroupsByProductId;
    }

    private Map<String, List<TagData>> groupTagsByProductId(List<String> productIds) {
        return productQueryMapper.findTagsByProductIds(productIds).stream()
                .collect(Collectors.groupingBy(ProductTagNameRow::productId, Collectors.mapping(this::toTagData, Collectors.toList())));
    }

    private ProductSummaryResult toProductSummaryResult(ProductWithCategoryRow row, List<TagData> tags) {
        return new ProductSummaryResult(
                ProductId.of(row.productId()),
                row.name(),
                row.imageUrl(),
                row.categoryId() != null ? new CategoryData(CategoryId.of(row.categoryId()), row.categoryName()) : null,
                row.price(),
                ProductKind.of(row.kind()),
                row.branchId() != null ? BranchId.of(row.branchId()) : null,
                ProductStatus.of(row.status()),
                tags
        );
    }

    private ProductDetailResult toProductDetailResult(
            ProductWithCategoryRow row,
            List<TagData> tags,
            List<LinkedOptionGroupData> optionGroups
    ) {
        return new ProductDetailResult(
                ProductId.of(row.productId()),
                row.name(),
                row.description(),
                row.imageUrl(),
                row.categoryId() != null ? new CategoryData(CategoryId.of(row.categoryId()), row.categoryName()) : null,
                row.price(),
                row.kcal(),
                row.allergenInfo() != null ? AllergenInfo.of(row.allergenInfo()) : null,
                ProductKind.of(row.kind()),
                row.branchId() != null ? BranchId.of(row.branchId()) : null,
                ProductStatus.of(row.status()),
                tags,
                optionGroups,
                row.createdAt()
        );
    }

    private LinkedOptionGroupData toLinkedOptionGroupData(ProductOptionGroupItemRow row, List<OptionItemData> items) {
        return new LinkedOptionGroupData(
                ProductId.of(row.productId()),
                OptionGroupId.of(row.optionGroupId()),
                row.groupName(),
                row.groupDescription(),
                row.isRequired(),
                row.allowMultiple(),
                items
        );
    }

    private OptionItemData toOptionItemData(ProductOptionGroupItemRow row) {
        return new OptionItemData(row.itemName(), row.itemDescription(), row.itemPrice(), row.itemCreatedAt());
    }

    private TagData toTagData(ProductTagNameRow row) {
        return new TagData(TagId.of(row.tagId()), row.tagName());
    }
}
