package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.dto.ProductDetailResult;
import com.dozycoffee.product.application.dto.ProductFilterQuery;
import com.dozycoffee.product.application.repository.ProductQueryRepository;

import java.util.ArrayList;
import java.util.List;

public class FakeProductQueryRepository implements ProductQueryRepository {

    private final List<ProductDetailResult> store = new ArrayList<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public void add(ProductDetailResult result) {
        store.add(result);
    }

    @Override
    public List<ProductDetailResult> findByFilter(ProductFilterQuery filter) throws RepositoryException {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
        return new ArrayList<>(store);
    }
}
