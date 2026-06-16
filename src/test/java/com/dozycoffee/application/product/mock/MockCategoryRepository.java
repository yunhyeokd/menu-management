package com.dozycoffee.application.product.mock;

import com.dozycoffee.application.product.CategoryRepository;
import com.dozycoffee.domain.product.Category;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockCategoryRepository implements CategoryRepository {

    private final Map<Long, Category> categoryMap = new ConcurrentHashMap<>();
    private volatile long nextId = 1;

    public MockCategoryRepository() {

    }

    private void mockData() {

    }

    @Override
    public Category save(Category category) {
        synchronized (categoryMap) {
            if (category.getId() == null) {
                Category newCategory = Category.of(nextId++, category.getName(), category.getCreatedAt());
                categoryMap.put(newCategory.getId(), newCategory);
                return newCategory;
            }
            else {
                if (!categoryMap.containsKey(category.getId())) {
                    throw new RuntimeException("Category with id " + category.getId() + " does not exist");
                }
                categoryMap.put(category.getId(), category);
                return category;
            }
        }
    }

    @Override
    public List<Category> findAll() {
        return List.of();
    }

    @Override
    public List<Category> findAllByName(String name) {
        return List.of();
    }

    @Override
    public Category findById(long id) {
        return categoryMap.get(id);
    }

    @Override
    public Category findByName(String name) {
        for (Category category : categoryMap.values()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public void deleteById(long id) {
        categoryMap.remove(id);
    }
}
