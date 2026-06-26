package com.dozycoffee.branch.application;

import com.dozycoffee.branch.application.model.BranchProduct;
import com.dozycoffee.branch.domain.*;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.core.application.exception.*;
import com.dozycoffee.product.domain.ProductId;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public List<BranchProduct> findOverridableProducts(BranchId branchId) {
        try {
            assertBranchExists(branchId);
            Set<BranchProduct> overridableProducts = new HashSet<>();
            overridableProducts.addAll(productQueryPort.findAllActiveCommon());
            overridableProducts.addAll(productQueryPort.findAllActiveBranchExclusive(branchId));
            return overridableProducts.stream().toList();
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
