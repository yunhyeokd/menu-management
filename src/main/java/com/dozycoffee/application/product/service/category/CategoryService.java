package com.dozycoffee.application.product.service.category;

import com.dozycoffee.application.common.IdentifierGenerator;
import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.repository.CategoryRepository;
import com.dozycoffee.application.product.repository.ProductRepository;
import com.dozycoffee.domain.product.Category;
import com.dozycoffee.domain.product.CategoryId;
import com.dozycoffee.domain.product.Product;
import com.dozycoffee.domain.product.ProductException;

import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final IdentifierGenerator<CategoryId> idGenerator;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            IdentifierGenerator<CategoryId> idGenerator
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.idGenerator = idGenerator;
    }

    public Category create(String categoryName) {
        try {
            if (categoryRepository.findByName(categoryName).isPresent()) {
                throw CategoryBusinessException.with(CategoryErrors.DUPLICATE_NAME_ERROR);
            }
            try {
                Category category = Category.create(idGenerator.generate(), categoryName);
                categoryRepository.save(category);
                return category;
            } catch (ProductException e) {
                throw CategoryBusinessException.with(CategoryErrors.INVALID_CATEGORY_ERROR);
            }
        } catch (RepositoryException e) {
            throw CategoryBusinessException.with(CategoryErrors.UNKNOWN_ERROR);
        }
    }

    public Category updateName(CategoryId id, String newName) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> CategoryBusinessException.with(CategoryErrors.NOT_FOUND_ERROR));
            if (categoryRepository.findByName(newName).isPresent()) {
                throw CategoryBusinessException.with(CategoryErrors.DUPLICATE_NAME_ERROR);
            }
            try {
                category.updateName(newName);
                categoryRepository.save(category);
                return category;
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

    public void remove(CategoryId categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> CategoryBusinessException.with(CategoryErrors.NOT_FOUND_ERROR));
            List<Product> products = productRepository.findAllByCategoryId(category.getId());
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
