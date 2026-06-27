package com.dozycoffee.product.application.service;

import com.dozycoffee.core.domain.IdentifierGenerator;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.ProductServiceCode;
import com.dozycoffee.core.application.exception.*;
import com.dozycoffee.product.application.dto.*;
import com.dozycoffee.product.application.repository.*;
import com.dozycoffee.product.domain.*;

import java.util.*;

public class ProductService {

    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final CategoryRepository categoryRepository;
    private final BranchExistencePort branchExistencePort;
    private final IdentifierGenerator<ProductId> idGenerator;

    public ProductService(
            ProductRepository productRepository,
            ProductQueryRepository productQueryRepository,
            CategoryRepository categoryRepository,
            BranchExistencePort branchExistencePort,
            IdentifierGenerator<ProductId> idGenerator
    ) {
        this.productRepository = productRepository;
        this.productQueryRepository = productQueryRepository;
        this.categoryRepository = categoryRepository;
        this.branchExistencePort = branchExistencePort;
        this.idGenerator = idGenerator;
    }

    public void assertExists(ProductId productId) {
        try {
            if (!productRepository.existsById(productId))
                throw new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.PRODUCT_NOT_FOUND_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    private Product getProduct(ProductId id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    public Product register(ProductRegisterCommand command) {
        try {
            categoryRepository.findById(command.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            if (command.branchId() != null && !branchExistencePort.existsById(command.branchId())) {
                throw new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.BRANCH_NOT_FOUND_ERROR);
            }
            ProductId productId = idGenerator.generate();
            Product product = Product.create(
                    productId,
                    command.name(),
                    command.description(),
                    command.imageUrl(),
                    command.categoryId(),
                    command.price(),
                    command.kcal(),
                    command.allergenInfo(),
                    command.kind(),
                    command.branchId()
            );
            productRepository.save(product);
            return product;
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public Product findById(ProductId productId) {
        try {
            return getProduct(productId);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<ProductDetailResult> searchProducts(ProductFilterQuery query) {
        try {
            return productQueryRepository.findByFilter(query);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public Product updateProfile(ProductProfileUpdateCommand command) {
        try {
            Product product = getProduct(command.id());
            categoryRepository
                    .findById(command.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            product.updateName(command.name());
            product.updateDescription(command.description());
            product.updateImageUrl(command.imageUrl());
            product.changeCategory(command.categoryId());
            product.updatePrice(command.price());
            product.updateKcal(command.kcal());
            product.updateAllergenInfo(command.allergenInfo());
            productRepository.save(product);
            return product;
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void deleteById(ProductId productId) {
        try {
            assertExists(productId);
            productRepository.deleteById(productId);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void activate(ProductId productId) {
        try {
            Product product = getProduct(productId);
            product.activate();
            productRepository.save(product);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void deactivate(ProductId productId) {
        try {
            Product product = getProduct(productId);
            product.deactivate();
            productRepository.save(product);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}
