package com.dozycoffee.application.branch.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.branch.BranchId;
import com.dozycoffee.domain.branch.ProductSalesOverride;
import com.dozycoffee.domain.product.ProductId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class FakeProductSalesOverrideRepository implements ProductSalesOverrideRepository {

    private final Map<Long, ProductSalesOverride> store = new LinkedHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public ProductSalesOverride put(ProductSalesOverride override) {
        store.put(override.getId(), override);
        return override;
    }

    public boolean contains(Long id) {
        return store.containsKey(id);
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
        if (productSalesOverride.getId() == null) {
            long newId = idSeq.getAndIncrement();
            ProductSalesOverride withId = ProductSalesOverride.of(
                    newId,
                    productSalesOverride.getProductId(),
                    productSalesOverride.getBranchId(),
                    productSalesOverride.getStatus(),
                    productSalesOverride.getCreatedAt()
            );
            store.put(newId, withId);
        } else {
            store.put(productSalesOverride.getId(), productSalesOverride);
        }
    }

    @Override
    public ProductSalesOverride findByBranchIdAndProductId(BranchId branchId, ProductId productId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(o -> o.getBranchId().equals(branchId) && o.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ProductSalesOverride findById(Long id) throws RepositoryException {
        checkThrow();
        return store.get(id);
    }

    @Override
    public void deleteById(Long id) throws RepositoryException {
        checkThrow();
        store.remove(id);
    }
}
