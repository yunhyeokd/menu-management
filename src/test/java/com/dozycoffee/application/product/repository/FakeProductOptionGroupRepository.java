package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.OptionGroupId;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductOptionGroup;
import com.dozycoffee.domain.product.ProductOptionGroupId;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductOptionGroupRepository implements ProductOptionGroupRepository {

    private final Map<ProductOptionGroupId, ProductOptionGroup> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public ProductOptionGroup put(ProductOptionGroup productOptionGroup) {
        store.put(productOptionGroup.getId(), productOptionGroup);
        return productOptionGroup;
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(ProductOptionGroup productOptionGroup) throws RepositoryException {
        checkThrow();
        store.put(productOptionGroup.getId(), productOptionGroup);
    }

    @Override
    public Optional<ProductOptionGroup> findById(ProductOptionGroupId id) throws RepositoryException {
        checkThrow();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ProductOptionGroup> findAllByOptionGroupId(OptionGroupId id) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(pog -> pog.getOptionGroupId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductOptionGroup> findByProductIdAndOptionGroupId(ProductId productId, OptionGroupId optionGroupId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(pog -> pog.getProductId().equals(productId) && pog.getOptionGroupId().equals(optionGroupId))
                .findFirst();
    }

    @Override
    public void deleteAllByProductId(ProductId productId) throws RepositoryException {
        checkThrow();
        store.values().removeIf(pog -> pog.getProductId().equals(productId));
    }
}
