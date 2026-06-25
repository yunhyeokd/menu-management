package com.dozycoffee.application.product.service.category;

import com.dozycoffee.application.common.IdentifierGenerator;
import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.common.ServiceCode;
import com.dozycoffee.application.common.exception.ConflictException;
import com.dozycoffee.application.common.exception.ResourceNotFoundException;
import com.dozycoffee.application.common.exception.SystemException;
import com.dozycoffee.application.common.exception.ValidationException;
import com.dozycoffee.application.product.dto.CategoryData;
import com.dozycoffee.application.product.repository.CategoryRepository;
import com.dozycoffee.application.product.repository.ProductRepository;
import com.dozycoffee.application.product.service.ProductErrors;
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

    public CategoryData create(String categoryName) {
        try {
            if (categoryRepository.findByName(categoryName).isPresent()) {
                throw new ConflictException(ServiceCode.PRD, ProductErrors.DUPLICATE_CATEGORY_NAME_ERROR);
            }
            try {
                Category category = Category.create(idGenerator.generate(), categoryName);
                categoryRepository.save(category);
                return CategoryData.from(category);
            } catch (ProductException e) {
                throw new ValidationException(ServiceCode.PRD, ProductErrors.INVALID_CATEGORY_ERROR);
            }
        } catch (RepositoryException e) {
            throw new SystemException(ServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public CategoryData updateName(CategoryId id, String newName) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(ServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            if (categoryRepository.findByName(newName).isPresent()) {
                throw new ConflictException(ServiceCode.PRD, ProductErrors.DUPLICATE_CATEGORY_NAME_ERROR);
            }
            try {
                category.updateName(newName);
                categoryRepository.save(category);
                return CategoryData.from(category);
            } catch (ProductException e) {
                throw new ValidationException(ServiceCode.PRD, ProductErrors.INVALID_CATEGORY_ERROR);
            }
        } catch (RepositoryException e) {
            throw new SystemException(ServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<CategoryData> findAll() {
        try {
            return categoryRepository.findAll().stream()
                    .map(CategoryData::from)
                    .toList();
        } catch (RepositoryException e) {
            throw new SystemException(ServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<CategoryData> searchByName(String categoryName) {
        try {
            return categoryRepository.searchByName(categoryName).stream()
                    .map(CategoryData::from)
                    .toList();
        } catch (RepositoryException e) {
            throw new SystemException(ServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void remove(CategoryId categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(ServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            List<Product> products = productRepository.findAllByCategoryId(category.getId());
            for (Product product : products) {
                product.deactivate();
                productRepository.save(product);
            }
            categoryRepository.deleteById(categoryId);
        } catch (RepositoryException e) {
            throw new SystemException(ServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}
