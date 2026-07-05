package com.dozycoffee.branch.application;

import com.dozycoffee.branch.application.model.BranchProduct;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.product.domain.ProductId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeBranchProductQueryPort implements BranchProductQueryPort {

    private final Map<ProductId, BranchProduct> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void put(BranchProduct product) {
        store.put(product.getProductId(), product);
    }

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public Optional<BranchProduct> findById(ProductId productId) {
        checkThrow();
        return Optional.ofNullable(store.get(productId));
    }

    @Override
    public List<BranchProduct> findOverridableProducts(BranchId branchId) {
        checkThrow();
        return store.values().stream()
                .filter(p -> p.isActive() && (p.isCommon() || branchId.equals(p.getBranchId())))
                .toList();
    }
}
