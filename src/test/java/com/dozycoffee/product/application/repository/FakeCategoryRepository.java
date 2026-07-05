package com.dozycoffee.product.application.repository;

import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.product.application.repository.CategoryRepository;
import com.dozycoffee.product.domain.Category;
import com.dozycoffee.product.domain.CategoryId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeCategoryRepository implements CategoryRepository {

    private final Map<CategoryId, Category> store = new LinkedHashMap<>();
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public Category put(Category category) {
        store.put(category.getId(), category);
        return category;
    }

    public boolean contains(CategoryId id) {
        return store.containsKey(id);
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public void save(Category category) throws RepositoryException {
        checkThrow();
        store.put(category.getId(), category);
    }

    @Override
    public List<Category> findAll() throws RepositoryException {
        checkThrow();
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Category> searchByName(String name) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(c -> c.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findById(CategoryId id) throws RepositoryException {
        checkThrow();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Category> findByName(String name) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    @Override
    public void deleteById(CategoryId id) throws RepositoryException {
        checkThrow();
        store.remove(id);
    }
}
