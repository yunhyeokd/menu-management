package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.ProductTag;
import com.dozycoffee.domain.product.TagId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FakeProductTagRepository implements ProductTagRepository {

    private final Map<Long, ProductTag> store = new LinkedHashMap<>();
    private long sequence = 1;
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public ProductTag add(ProductTag productTag) {
        store.put(productTag.getId(), productTag);
        return productTag;
    }

    public List<ProductTag> all() {
        return new ArrayList<>(store.values());
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void deleteAllByTagId(TagId tagId) throws RepositoryException {
        checkThrow();
        store.entrySet().removeIf(entry -> entry.getValue().getTagId().equals(tagId));
    }

    @Override
    public List<ProductTag> findAllByTagId(TagId tagId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(productTag -> productTag.getTagId().equals(tagId))
                .collect(Collectors.toList());
    }
}
