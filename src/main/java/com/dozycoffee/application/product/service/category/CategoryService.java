package com.dozycoffee.application.product.service.category;

import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.repository.CategoryRepository;
import com.dozycoffee.application.product.repository.ProductRepository;
import com.dozycoffee.domain.product.Category;
import com.dozycoffee.domain.product.Product;
import com.dozycoffee.domain.product.ProductException;

import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public Category create(String categoryName) {
        try {
            Category category = categoryRepository.findByName(categoryName);
            if (category != null) {
                throw CategoryBusinessException.with(CategoryErrors.DUPLICATE_NAME_ERROR);
            }
            try {
                category = Category.create(categoryName);
                category = categoryRepository.save(category);
                return category;
            } catch (ProductException e) {
                throw CategoryBusinessException.with(CategoryErrors.INVALID_CATEGORY_ERROR);
            }
        } catch (RepositoryException e) {
            throw CategoryBusinessException.with(CategoryErrors.UNKNOWN_ERROR);
        }
    }

    public Category updateName(long id, String newName) {
        try {
            Category category = categoryRepository.findById(id);
            if (category == null) {
                throw CategoryBusinessException.with(CategoryErrors.NOT_FOUND_ERROR);
            }
            Category newCategory = categoryRepository.findByName(newName);
            if (newCategory != null) {
                throw CategoryBusinessException.with(CategoryErrors.DUPLICATE_NAME_ERROR);
            }
            try {
                category.updateName(newName);
                return categoryRepository.save(category);
            } catch (ProductException e) {
                throw CategoryBusinessException.with(CategoryErrors.INVALID_CATEGORY_ERROR);
            }
        } catch (RepositoryException e) {
            throw CategoryBusinessException.with(CategoryErrors.UNKNOWN_ERROR);
        }
    }

    public List<Category> findAll() {
        try {
            return categoryRepository.findAll();
        } catch (RepositoryException e) {
            throw CategoryBusinessException.with(CategoryErrors.UNKNOWN_ERROR);
        }
    }

    public List<Category> searchByName(String categoryName) {
        try {
            return categoryRepository.searchByName(categoryName);
        } catch (RepositoryException e) {
            throw CategoryBusinessException.with(CategoryErrors.UNKNOWN_ERROR);
        }
    }

    public void remove(long categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId);
            if (category == null) {
                throw CategoryBusinessException.with(CategoryErrors.NOT_FOUND_ERROR);
            }
            List<Product> products = productRepository.findAllByCategoryId(categoryId);
            for (Product product : products) {
                product.deactivate();
                productRepository.save(product);
            }
            categoryRepository.deleteById(categoryId);
        } catch (RepositoryException e) {
            throw CategoryBusinessException.with(CategoryErrors.UNKNOWN_ERROR);
        }
    }
}
