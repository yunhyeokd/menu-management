package com.dozycoffee.catalog.application.repository;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.override.ProductSalesOverride;
import com.dozycoffee.core.exception.base.RepositoryException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeProductSalesOverrideRepository implements ProductSalesOverrideRepository {

    private final Map<String, ProductSalesOverride> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public ProductSalesOverride put(ProductSalesOverride override) {
        store.put(key(override.getBranchId(), override.getProductId()), override);
        return override;
    }

    public boolean contains(BranchId branchId, ProductId productId) {
        return store.containsKey(key(branchId, productId));
    }

    private String key(BranchId branchId, ProductId productId) {
        return branchId.getValue() + "_" + productId.getValue();
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(ProductSalesOverride productSalesOverride) throws RepositoryException {
        checkThrow();
        store.put(key(productSalesOverride.getBranchId(), productSalesOverride.getProductId()), productSalesOverride);
    }

    @Override
    public Optional<ProductSalesOverride> findByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException {
        checkThrow();
        return Optional.ofNullable(store.get(key(branchId, productId)));
    }

    @Override
    public List<ProductSalesOverride> findAllByBranchId(BranchId branchId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(override -> override.getBranchId().equals(branchId))
                .toList();
    }

    @Override
    public void deleteByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException {
        checkThrow();
        store.remove(key(branchId, productId));
    }
}
