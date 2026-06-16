package com.dozycoffee.application.product;

import com.dozycoffee.domain.product.Category;
import com.dozycoffee.domain.product.ProductException;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(String name) {
        Category category = categoryRepository.findByName(name);
        if (category != null) {
            throw new CategoryServiceException("category name duplicated");
        }
        try {
            category = Category.create(name);
            category = categoryRepository.save(category);
            return category;
        } catch (ProductException e) {
            throw new CategoryServiceException(e);
        }
    }

    public Category updateName(long id, String newName) {
        try {
            Category category = categoryRepository.findById(id);
            if (category == null) {
                throw new CategoryServiceException("category not found");
            }
            category.updateName(newName);
            return categoryRepository.save(category);
        } catch (ProductException e) {
            throw new CategoryServiceException(e);
        }
    }

    public void removeCategory(long id) {
        categoryRepository.deleteById(id);
    }
}
