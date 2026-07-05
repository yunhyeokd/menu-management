package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.application.repository.ProductRepository;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.Product;
import com.dozycoffee.product.domain.ProductId;
import com.dozycoffee.product.domain.ProductStatus;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {

    private final Map<ProductId, Product> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public Product put(Product product) {
        store.put(product.getId(), product);
        return product;
    }

    public List<Product> all() {
        return new ArrayList<>(store.values());
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(Product product) throws RepositoryException {
        checkThrow();
        store.put(product.getId(), product);
    }

    @Override
    public boolean existsById(ProductId productId) throws RepositoryException {
        checkThrow();
        return store.containsKey(productId);
    }

    @Override
    public Optional<Product> findById(ProductId productId) throws RepositoryException {
        checkThrow();
        return Optional.ofNullable(store.get(productId));
    }

    @Override
    public List<Product> findAllByCategoryId(CategoryId categoryId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(p -> p.getCategoryId() != null && p.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllActiveCommon() throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE && p.getBranchId() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllActiveBranchExclusive(BranchId branchId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE && branchId.equals(p.getBranchId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findActiveById(ProductId productId) throws RepositoryException {
        checkThrow();
        return Optional.ofNullable(store.get(productId))
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE);
    }

    @Override
    public void updateStatusByBranchId(BranchId branchId, ProductStatus status) throws RepositoryException {
        checkThrow();
        store.values().stream()
                .filter(p -> branchId.equals(p.getBranchId()))
                .forEach(p -> {
                    if (status == ProductStatus.ACTIVE) p.activate();
                    else p.deactivate();
                });
    }

    @Override
    public void deleteAllByBranchId(BranchId branchId) throws RepositoryException {
        checkThrow();
        store.entrySet().removeIf(e -> branchId.equals(e.getValue().getBranchId()));
    }

    @Override
    public void deleteById(ProductId productId) throws RepositoryException {
        checkThrow();
        store.remove(productId);
    }
}
