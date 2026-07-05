package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.product.application.dto.ProductDetailResult;
import com.dozycoffee.product.application.dto.ProductFilterQuery;
import com.dozycoffee.product.application.dto.ProductSummaryResult;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.domain.ProductId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeProductQueryRepository implements ProductQueryRepository {

    private final List<ProductSummaryResult> summaryStore = new ArrayList<>();
    private final Map<ProductId, ProductDetailResult> detailStore = new LinkedHashMap<>();
    private final Map<ProductId, List<TagData>> tagsByProductId = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public void add(ProductSummaryResult result) {
        summaryStore.add(result);
    }

    public void add(ProductDetailResult result) {
        detailStore.put(result.id(), result);
    }

    public void putTags(ProductId productId, List<TagData> tags) {
        tagsByProductId.put(productId, tags);
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public List<ProductSummaryResult> findByFilter(ProductFilterQuery filter) throws RepositoryException {
        checkThrow();
        return new ArrayList<>(summaryStore);
    }

    @Override
    public Optional<ProductDetailResult> findDetailById(ProductId productId) throws RepositoryException {
        checkThrow();
        return Optional.ofNullable(detailStore.get(productId));
    }

    @Override
    public Map<ProductId, List<TagData>> findTagsByProductIds(List<ProductId> productIds) throws RepositoryException {
        checkThrow();
        Map<ProductId, List<TagData>> result = new LinkedHashMap<>();
        for (ProductId id : productIds) {
            if (tagsByProductId.containsKey(id)) result.put(id, tagsByProductId.get(id));
        }
        return result;
    }
}
