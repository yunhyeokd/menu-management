package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.ProductOptionGroupItemRow;
import com.dozycoffee.infrastructure.persistance.entity.ProductTagNameRow;
import com.dozycoffee.infrastructure.persistance.entity.ProductWithCategoryRow;
import com.dozycoffee.infrastructure.persistance.mapper.ProductQueryMapper;
import com.dozycoffee.product.application.dto.*;
import com.dozycoffee.product.application.repository.ProductQueryRepository;
import com.dozycoffee.product.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyBatisProductQueryRepository implements ProductQueryRepository {

    private final ProductQueryMapper productQueryMapper;

    @Override
    public List<ProductDetailResult> findByFilter(ProductFilterQuery filter) throws RepositoryException {
        List<ProductWithCategoryRow> products = productQueryMapper.findByFilter(filter);
        if (products.isEmpty()) return List.of();

        List<String> productIds = products.stream().map(ProductWithCategoryRow::productId).toList();

        Map<String, List<TagData>> tagsByProductId = productQueryMapper.findTagsByProductIds(productIds).stream()
                .collect(Collectors.groupingBy(
                        ProductTagNameRow::productId,
                        Collectors.mapping(
                                row -> new TagData(TagId.of(row.tagId()), row.tagName()),
                                Collectors.toList()
                        )
                ));

        Map<String, Map<String, List<OptionItemData>>> itemsByProductAndGroup =
                productQueryMapper.findOptionGroupsByProductIds(productIds).stream()
                        .collect(Collectors.groupingBy(
                                ProductOptionGroupItemRow::productId,
                                Collectors.groupingBy(
                                        ProductOptionGroupItemRow::optionGroupId,
                                        Collectors.mapping(
                                                row -> new OptionItemData(row.itemName(), row.itemDescription(), row.itemPrice(), row.itemCreatedAt()),
                                                Collectors.toList()
                                        )
                                )
                        ));

        Map<String, List<LinkedOptionGroupData>> optionGroupsByProductId = new LinkedHashMap<>();
        productQueryMapper.findOptionGroupsByProductIds(productIds).stream()
                .collect(Collectors.groupingBy(
                        row -> row.productId() + ":" + row.optionGroupId()
                ))
                .forEach((key, rows) -> {
                    ProductOptionGroupItemRow first = rows.get(0);
                    String productId = first.productId();
                    List<OptionItemData> items = itemsByProductAndGroup
                            .getOrDefault(productId, Map.of())
                            .getOrDefault(first.optionGroupId(), List.of());
                    LinkedOptionGroupData data = new LinkedOptionGroupData(
                            ProductId.of(productId),
                            OptionGroupId.of(first.optionGroupId()),
                            first.groupName(),
                            first.groupDescription(),
                            first.isRequired(),
                            first.allowMultiple(),
                            items
                    );
                    optionGroupsByProductId.computeIfAbsent(productId, k -> new ArrayList<>()).add(data);
                });

        return products.stream().map(row -> new ProductDetailResult(
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
                tagsByProductId.getOrDefault(row.productId(), List.of()),
                optionGroupsByProductId.getOrDefault(row.productId(), List.of()),
                row.createdAt()
        )).toList();
    }
}
