package com.dozycoffee.application.product.repository;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.domain.product.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {

    private final Map<Long, Product> store = new LinkedHashMap<>();
    private long sequence = 1;
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
    public Product save(Product product) throws RepositoryException {
        checkThrow();
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> findAllByCategoryId(long categoryId) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(p -> p.getCategoryId() != null && p.getCategoryId() == categoryId)
                .collect(Collectors.toList());
    }
}
