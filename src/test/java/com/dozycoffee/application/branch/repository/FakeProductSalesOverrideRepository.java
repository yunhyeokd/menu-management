package com.dozycoffee.application.branch.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.branch.ProductSalesOverride;
import com.dozycoffee.domain.product.ProductId;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public ProductSalesOverride findByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException {
        checkThrow();
        return store.get(key(branchId, productId));
    }

    @Override
    public void deleteByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException {
        checkThrow();
        store.remove(key(branchId, productId));
    }
}
