package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductTag;
import com.dozycoffee.domain.product.Tag;
import com.dozycoffee.domain.product.TagId;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductTagRepository implements ProductTagRepository {

    private final Map<Long, ProductTag> store = new LinkedHashMap<>();
    private final Map<TagId, Tag> tagStore = new LinkedHashMap<>();
    private long sequence = 1;
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public ProductTag add(ProductTag productTag) {
        store.put(productTag.getId(), productTag);
        return productTag;
    }

    public void registerTag(Tag tag) {
        tagStore.put(tag.getId(), tag);
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
    public void save(ProductTag productTag) throws RepositoryException {
        checkThrow();
        store.put(productTag.getId(), productTag);
    }

    @Override
    public void deleteAllByTagId(TagId tagId) throws RepositoryException {
        checkThrow();
        store.entrySet().removeIf(entry -> entry.getValue().getTagId().equals(tagId));
    }

    @Override
    public void deleteAllByProductId(ProductId productId) throws RepositoryException {
        checkThrow();
        store.entrySet().removeIf(entry -> entry.getValue().getProductId().equals(productId));
    }

    @Override
    public List<ProductTag> findAllByTagId(TagId tagId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(productTag -> productTag.getTagId().equals(tagId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findTagsByProductId(ProductId productId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(pt -> pt.getProductId().equals(productId))
                .map(pt -> tagStore.get(pt.getTagId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
