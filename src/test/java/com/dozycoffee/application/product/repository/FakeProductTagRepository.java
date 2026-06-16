package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.ProductTag;

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
        if (productTag.getId() == null) {
            ProductTag saved = ProductTag.of(
                    sequence++, productTag.getProductId(), productTag.getTagId(), productTag.getCreatedAt());
            store.put(saved.getId(), saved);
            return saved;
        }
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
    public void deleteAllByTagId(long tagId) throws RepositoryException {
        checkThrow();
        store.entrySet().removeIf(entry -> entry.getValue().getTagId() == tagId);
    }

    @Override
    public List<ProductTag> findAllByTagId(long tagId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(productTag -> productTag.getTagId() == tagId)
                .collect(Collectors.toList());
    }
}
