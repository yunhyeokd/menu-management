package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductTag;
import com.dozycoffee.domain.product.Tag;
import com.dozycoffee.domain.product.TagId;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductTagRepository implements ProductTagRepository {

    private final Map<String, ProductTag> store = new LinkedHashMap<>();
    private final Map<TagId, Tag> tagStore = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public ProductTag add(ProductTag productTag) {
        store.put(key(productTag.getProductId(), productTag.getTagId()), productTag);
        return productTag;
    }

    public void registerTag(Tag tag) {
        tagStore.put(tag.getId(), tag);
    }

    public List<ProductTag> all() {
        return new ArrayList<>(store.values());
    }

    private String key(ProductId productId, TagId tagId) {
        return productId.getValue() + "_" + tagId.getValue();
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
        store.put(key(productTag.getProductId(), productTag.getTagId()), productTag);
    }

    @Override
    public void deleteAllByTagId(TagId tagId) throws RepositoryException {
        checkThrow();
        store.values().removeIf(pt -> pt.getTagId().equals(tagId));
    }

    @Override
    public void deleteAllByProductId(ProductId productId) throws RepositoryException {
        checkThrow();
        store.values().removeIf(pt -> pt.getProductId().equals(productId));
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
