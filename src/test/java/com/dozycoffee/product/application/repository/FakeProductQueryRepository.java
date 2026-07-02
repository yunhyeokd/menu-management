package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.dto.ProductDetailResult;
import com.dozycoffee.product.application.dto.ProductFilterQuery;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.repository.ProductQueryRepository;
import com.dozycoffee.product.domain.ProductId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FakeProductQueryRepository implements ProductQueryRepository {

    private final List<ProductDetailResult> store = new ArrayList<>();
    private final Map<ProductId, List<TagData>> tagsByProductId = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public void add(ProductDetailResult result) {
        store.add(result);
    }

    public void putTags(ProductId productId, List<TagData> tags) {
        tagsByProductId.put(productId, tags);
    }

    @Override
    public List<ProductDetailResult> findByFilter(ProductFilterQuery filter) throws RepositoryException {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
        return new ArrayList<>(store);
    }

    @Override
    public Map<ProductId, List<TagData>> findTagsByProductIds(List<ProductId> productIds) throws RepositoryException {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
        Map<ProductId, List<TagData>> result = new LinkedHashMap<>();
        for (ProductId id : productIds) {
            if (tagsByProductId.containsKey(id)) result.put(id, tagsByProductId.get(id));
        }
        return result;
    }
}
