package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.repository.ProductOptionGroupRepository;
import com.dozycoffee.product.domain.OptionGroupId;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductOptionGroup;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductOptionGroupRepository implements ProductOptionGroupRepository {

    private final Map<String, ProductOptionGroup> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public ProductOptionGroup put(ProductOptionGroup productOptionGroup) {
        store.put(key(productOptionGroup.getProductId(), productOptionGroup.getOptionGroupId()), productOptionGroup);
        return productOptionGroup;
    }

    private String key(ProductId productId, OptionGroupId optionGroupId) {
        return productId.getValue() + "_" + optionGroupId.getValue();
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
        store.put(key(productOptionGroup.getProductId(), productOptionGroup.getOptionGroupId()), productOptionGroup);
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
        return Optional.ofNullable(store.get(key(productId, optionGroupId)));
    }

    @Override
    public void deleteAllByProductId(ProductId productId) throws RepositoryException {
        checkThrow();
        store.values().removeIf(pog -> pog.getProductId().equals(productId));
    }
}
