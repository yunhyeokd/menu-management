package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.branch.dto.BranchProfileUpdateDto;
import com.dozycoffee.application.branch.repository.BranchAccountRepository;
import com.dozycoffee.application.branch.repository.BranchProfileRepository;
import com.dozycoffee.application.branch.repository.ProductSalesOverrideRepository;
import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.repository.ProductRepository;
import com.dozycoffee.domain.branch.*;
import com.dozycoffee.domain.product.Product;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BranchService {

    private final BranchAccountRepository branchAccountRepository;
    private final BranchProfileRepository branchProfileRepository;
    private final ProductRepository productRepository;
    private final ProductSalesOverrideRepository productSalesOverrideRepository;

    public BranchService(
            BranchAccountRepository branchAccountRepository,
            BranchProfileRepository branchProfileRepository,
            ProductRepository productRepository,
            ProductSalesOverrideRepository productSalesOverrideRepository
    ) {
        this.branchAccountRepository = branchAccountRepository;
        this.branchProfileRepository = branchProfileRepository;
        this.productRepository = productRepository;
        this.productSalesOverrideRepository = productSalesOverrideRepository;
    }

    private void assertBranchAccountExists(BranchId branchId) throws RepositoryException {
        BranchAccount branchAccount = branchAccountRepository.findById(branchId);
        if (branchAccount == null) {
            throw BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR);
        }
    }

    private void assertProductExists(ProductId productId) throws RepositoryException {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw BranchBusinessException.with(BranchErrors.PRODUCT_NOT_FOUND_ERROR);
        }
    }

    private BranchAccount getBranchAccount(BranchId branchId) {
        BranchAccount branchAccount = branchAccountRepository.findById(branchId);
        if (branchAccount == null) {
            throw BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR);
        }
        return branchAccount;
    }

    private BranchProfile getBranchProfile(BranchId branchId) throws RepositoryException {
        BranchProfile branchProfile = branchProfileRepository.findById(branchId);
        if (branchProfile == null) {
            throw BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR);
        }
        return branchProfile;
    }

    private Product getProduct(ProductId productId) throws RepositoryException {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw BranchBusinessException.with(BranchErrors.PRODUCT_NOT_FOUND_ERROR);
        }
        return product;
    }

    public void updateProfile(BranchId branchId, BranchProfileUpdateDto updateDto) {
        try {
            BranchProfile branchProfile = getBranchProfile(branchId);
            try {
                branchProfile.changeName(updateDto.name());
                branchProfile.changeAddress(updateDto.address());
                branchProfileRepository.save(branchProfile);
            } catch (BranchException e) {
                throw BranchBusinessException.with(BranchErrors.INVALID_PROFILE_ERROR);
            }
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    public void softDelete(BranchId branchId) {
        try {
            BranchAccount branchAccount = getBranchAccount(branchId);
            try {
                productRepository.updateStatusByBranchId(branchId, ProductStatus.INACTIVE);
                branchAccount.softDelete();
                branchAccountRepository.save(branchAccount);
            } catch (BranchException e) {
                throw BranchBusinessException.with(BranchErrors.ALREADY_DELETED_ERROR);
            }
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    public void hardDelete(BranchId branchId) {
        try {
            productRepository.deleteAllByBranchId(branchId);
            branchProfileRepository.deleteById(branchId);
            branchAccountRepository.deleteById(branchId);
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    public List<Product> findOverridableProducts(BranchId branchId) {
        try {
            assertBranchAccountExists(branchId);
            Set<Product> overridableProducts = new HashSet<>();
            overridableProducts.addAll(productRepository.findAllActiveCommon());
            overridableProducts.addAll(productRepository.findAllActiveBranchExclusive(branchId));
            return overridableProducts.stream().toList();
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    private void suspendSale(BranchId branchId, ProductId productId, ProductSalesOverrideStatus overrideStatus) {
        try {
            ProductSalesOverride salesOverride = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId);
            try {
                if (salesOverride == null) {
                    salesOverride = ProductSalesOverride.create(productId, branchId, overrideStatus);
                } else if (overrideStatus != salesOverride.getStatus()) {
                    salesOverride.updateStatus(overrideStatus);
                } else return;
                productSalesOverrideRepository.save(salesOverride);
            } catch (BranchException e) {
                throw BranchBusinessException.with(BranchErrors.INVALID_BRANCH_ERROR);
            }
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    public void hideSale(BranchId branchId, ProductId productId) {
        try {
            assertBranchAccountExists(branchId);
            Product product = getProduct(productId);
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw BranchBusinessException.with(BranchErrors.PRODUCT_NOT_ACTIVE_ERROR);
            }
            suspendSale(branchId, productId, ProductSalesOverrideStatus.HIDDEN);
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    public void soldOut(BranchId branchId, ProductId productId) {
        try {
            assertBranchAccountExists(branchId);
            Product product = getProduct(productId);
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw BranchBusinessException.with(BranchErrors.PRODUCT_NOT_ACTIVE_ERROR);
            }
            suspendSale(branchId, productId, ProductSalesOverrideStatus.SOLD_OUT);
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    public void restoreSale(BranchId branchId, ProductId productId) {
        try {
            ProductSalesOverride productSalesOverride = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId);
            if (productSalesOverride == null) {
                throw BranchBusinessException.with(BranchErrors.SALES_OVERRIDE_NOT_FOUND_ERROR);
            }
            productSalesOverrideRepository.deleteById(productSalesOverride.getId());
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }
}
