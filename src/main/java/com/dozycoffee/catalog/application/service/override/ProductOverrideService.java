package com.dozycoffee.catalog.application.service.override;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.application.ProductServiceCode;
import com.dozycoffee.catalog.application.repository.BranchExistencePort;
import com.dozycoffee.catalog.application.repository.ProductRepository;
import com.dozycoffee.catalog.application.repository.ProductSalesOverrideRepository;
import com.dozycoffee.catalog.application.service.ProductErrors;
import com.dozycoffee.catalog.domain.Product;
import com.dozycoffee.catalog.domain.ProductException;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductStatus;
import com.dozycoffee.catalog.domain.override.ProductSalesOverride;
import com.dozycoffee.catalog.domain.override.ProductSalesOverrideStatus;
import com.dozycoffee.core.exception.base.RepositoryException;
import com.dozycoffee.core.exception.service.ConflictException;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.core.exception.service.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProductOverrideService {

    private final BranchExistencePort branchExistencePort;
    private final ProductRepository productRepository;
    private final ProductSalesOverrideRepository productSalesOverrideRepository;

    public ProductOverrideService(
            BranchExistencePort branchExistencePort,
            ProductRepository productRepository,
            ProductSalesOverrideRepository productSalesOverrideRepository
    ) {
        this.branchExistencePort = branchExistencePort;
        this.productRepository = productRepository;
        this.productSalesOverrideRepository = productSalesOverrideRepository;
    }

    private void assertBranchExists(BranchId branchId) throws RepositoryException {
        if (!branchExistencePort.existsById(branchId)) {
            throw new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.BRANCH_NOT_FOUND_ERROR);
        }
    }

    private Product getProduct(ProductId productId) throws RepositoryException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    private void suspendSale(BranchId branchId, ProductId productId, ProductSalesOverrideStatus overrideStatus) {
        try {
            Optional<ProductSalesOverride> existing = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId);
            if (existing.isEmpty()) {
                productSalesOverrideRepository.save(ProductSalesOverride.create(productId, branchId, overrideStatus));
            } else {
                ProductSalesOverride salesOverride = existing.get();
                if (overrideStatus == salesOverride.getStatus()) return;
                salesOverride.updateStatus(overrideStatus);
                productSalesOverrideRepository.save(salesOverride);
            }
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_OVERRIDE_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void hideSale(BranchId branchId, ProductId productId) {
        try {
            assertBranchExists(branchId);
            Product product = getProduct(productId);
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new ConflictException(ProductServiceCode.PRD, ProductErrors.PRODUCT_NOT_ACTIVE_ERROR);
            }
            suspendSale(branchId, productId, ProductSalesOverrideStatus.HIDDEN);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void soldOut(BranchId branchId, ProductId productId) {
        try {
            assertBranchExists(branchId);
            Product product = getProduct(productId);
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new ConflictException(ProductServiceCode.PRD, ProductErrors.PRODUCT_NOT_ACTIVE_ERROR);
            }
            suspendSale(branchId, productId, ProductSalesOverrideStatus.SOLD_OUT);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void restoreSale(BranchId branchId, ProductId productId) {
        try {
            productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId)
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.SALES_OVERRIDE_NOT_FOUND_ERROR));
            productSalesOverrideRepository.deleteByBranchIdAndProductId(branchId, productId);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }
}
