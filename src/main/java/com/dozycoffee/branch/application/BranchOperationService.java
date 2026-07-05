package com.dozycoffee.branch.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.branch.application.model.BranchProduct;
import com.dozycoffee.branch.domain.*;
import com.dozycoffee.core.exception.base.*;
import com.dozycoffee.core.exception.service.*;
import com.dozycoffee.product.domain.ProductId;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BranchOperationService {

    private final BranchRepository branchRepository;
    private final BranchProductQueryPort productQueryPort;
    private final ProductSalesOverrideRepository productSalesOverrideRepository;

    public BranchOperationService(
            BranchRepository branchRepository,
            BranchProductQueryPort productQueryPort,
            ProductSalesOverrideRepository productSalesOverrideRepository
    ) {
        this.branchRepository = branchRepository;
        this.productQueryPort = productQueryPort;
        this.productSalesOverrideRepository = productSalesOverrideRepository;
    }

    private void assertBranchExists(BranchId branchId) throws RepositoryException {
        branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException(BranchServiceCode.BRN, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    private BranchProduct getProduct(ProductId productId) throws RepositoryException {
        return productQueryPort.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(BranchServiceCode.BRN, BranchErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Transactional(readOnly = true)
    public List<BranchProduct> findOverridableProducts(BranchId branchId) {
        try {
            assertBranchExists(branchId);
            return productQueryPort.findOverridableProducts(branchId);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
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
        } catch (BranchException e) {
            throw new ValidationException(BranchServiceCode.BRN, BranchErrors.INVALID_BRANCH_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void hideSale(BranchId branchId, ProductId productId) {
        try {
            assertBranchExists(branchId);
            BranchProduct product = getProduct(productId);
            if (!product.isActive()) {
                throw new ConflictException(BranchServiceCode.BRN, BranchErrors.PRODUCT_NOT_ACTIVE_ERROR);
            }
            suspendSale(branchId, productId, ProductSalesOverrideStatus.HIDDEN);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void soldOut(BranchId branchId, ProductId productId) {
        try {
            assertBranchExists(branchId);
            BranchProduct product = getProduct(productId);
            if (!product.isActive()) {
                throw new ConflictException(BranchServiceCode.BRN, BranchErrors.PRODUCT_NOT_ACTIVE_ERROR);
            }
            suspendSale(branchId, productId, ProductSalesOverrideStatus.SOLD_OUT);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void restoreSale(BranchId branchId, ProductId productId) {
        try {
            productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId)
                    .orElseThrow(() -> new ResourceNotFoundException(BranchServiceCode.BRN, BranchErrors.SALES_OVERRIDE_NOT_FOUND_ERROR));
            productSalesOverrideRepository.deleteByBranchIdAndProductId(branchId, productId);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }
}
