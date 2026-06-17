package com.dozycoffee.application.product;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.repository.CategoryRepository;
import com.dozycoffee.domain.product.Category;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FakeCategoryRepository implements CategoryRepository {

    private final Map<Long, Category> store = new LinkedHashMap<>();
    private long sequence = 1;
    private boolean shouldThrow = false;

    public void throwOnNextCall() {
        this.shouldThrow = true;
    }

    public Category put(Category category) {
        store.put(category.getId(), category);
        return category;
    }

    public boolean contains(long id) {
        return store.containsKey(id);
    }

    private void checkThrow() {
        if (shouldThrow) {
            shouldThrow = false;
            throw new RepositoryException("forced failure");
        }
    }

    @Override
    public Category save(Category category) throws RepositoryException {
        checkThrow();
        if (category.getId() == null) {
            Category saved = Category.of(sequence++, category.getName(), category.getCreatedAt());
            store.put(saved.getId(), saved);
            return saved;
        }
        store.put(category.getId(), category);
        return category;
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
    public Category findById(long id) throws RepositoryException {
        checkThrow();
        return store.get(id);
    }

    @Override
    public Category findByName(String name) throws RepositoryException {
        checkThrow();
        return store.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(long id) throws RepositoryException {
        checkThrow();
        store.remove(id);
    }
}
