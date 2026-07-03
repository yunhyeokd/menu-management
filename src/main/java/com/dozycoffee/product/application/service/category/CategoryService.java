package com.dozycoffee.product.application.service.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.core.domain.IdentifierGenerator;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.ProductServiceCode;
import com.dozycoffee.core.application.exception.ConflictException;
import com.dozycoffee.core.application.exception.ResourceNotFoundException;
import com.dozycoffee.core.application.exception.SystemException;
import com.dozycoffee.core.application.exception.ValidationException;
import com.dozycoffee.product.application.dto.CategoryData;
import com.dozycoffee.product.application.repository.CategoryRepository;
import com.dozycoffee.product.application.repository.ProductRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.domain.Category;
import com.dozycoffee.product.domain.CategoryId;
import com.dozycoffee.product.domain.Product;
import com.dozycoffee.product.domain.ProductException;

import java.util.List;

@Service
@Transactional
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
                throw new ConflictException(ProductServiceCode.PRD, ProductErrors.DUPLICATE_CATEGORY_NAME_ERROR);
            }
            try {
                Category category = Category.create(idGenerator.generate(), categoryName);
                categoryRepository.save(category);
                return CategoryData.from(category);
            } catch (ProductException e) {
                throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_CATEGORY_ERROR);
            }
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public CategoryData updateName(CategoryId id, String newName) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            if (categoryRepository.findByName(newName).isPresent()) {
                throw new ConflictException(ProductServiceCode.PRD, ProductErrors.DUPLICATE_CATEGORY_NAME_ERROR);
            }
            try {
                category.updateName(newName);
                categoryRepository.save(category);
                return CategoryData.from(category);
            } catch (ProductException e) {
                throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_CATEGORY_ERROR);
            }
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    private CategoryData getCategory(CategoryId categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryData::from)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR)
                );
    }

    @Transactional(readOnly = true)
    public CategoryData findById(CategoryId categoryId) {
        try {
            return getCategory(categoryId);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryData> findAll() {
        try {
            return categoryRepository.findAll().stream()
                    .map(CategoryData::from)
                    .toList();
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryData> searchByName(String categoryName) {
        try {
            return categoryRepository.searchByName(categoryName).stream()
                    .map(CategoryData::from)
                    .toList();
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void remove(CategoryId categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            List<Product> products = productRepository.findAllByCategoryId(category.getId());
            for (Product product : products) {
                product.deactivate();
                productRepository.save(product);
            }
            categoryRepository.deleteById(categoryId);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}
