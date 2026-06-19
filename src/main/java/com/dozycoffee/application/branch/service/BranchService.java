package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.branch.dto.BranchProfileUpdateDto;
import com.dozycoffee.application.branch.repository.BranchAccountRepository;
import com.dozycoffee.application.branch.repository.BranchProfileRepository;
import com.dozycoffee.application.branch.repository.ProductSalesOverrideRepository;
import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.repository.ProductRepository;
import com.dozycoffee.domain.branch.*;
import com.dozycoffee.domain.product.Product;
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

    private void assertBranchAccountExists(long branchId) throws RepositoryException {
        BranchAccount branchAccount = branchAccountRepository.findById(branchId);
        if (branchAccount == null) {
            throw BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR);
        }
    }

    private void assertBranchProfileExists(long branchId) throws RepositoryException {
        BranchProfile branchProfile = branchProfileRepository.findById(branchId);
        if (branchProfile == null) {
            throw BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR);
        }
    }

    private void assertProductExists(long productId) throws RepositoryException {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw BranchBusinessException.with(BranchErrors.PRODUCT_NOT_FOUND_ERROR);
        }
    }

    private void assertProductSalesOverrideExists(long productSalesOverrideId) throws RepositoryException {
        ProductSalesOverride productSalesOverride = productSalesOverrideRepository.findById(productSalesOverrideId);
        if (productSalesOverride == null) {
            throw BranchBusinessException.with(BranchErrors.SALES_OVERRIDE_NOT_FOUND_ERROR);
        }
    }

    private ProductSalesOverride getProductSalesOverride(long productSalesOverrideId) throws RepositoryException {
        ProductSalesOverride productSalesOverride = productSalesOverrideRepository.findById(productSalesOverrideId);
        if (productSalesOverride == null) {
            throw BranchBusinessException.with(BranchErrors.SALES_OVERRIDE_NOT_FOUND_ERROR);
        }
        return productSalesOverride;
    }

    private BranchAccount getBranchAccount(long branchId) {
        BranchAccount branchAccount = branchAccountRepository.findById(branchId);
        if (branchAccount == null) {
            throw BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR);
        }
        return branchAccount;
    }

    private BranchProfile getBranchProfile(long branchId) {
        BranchProfile branchProfile = branchProfileRepository.findById(branchId);
        if (branchProfile == null) {
            throw BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR);
        }
        return branchProfile;
    }

    private Product getProduct(long productId) {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw BranchBusinessException.with(BranchErrors.PRODUCT_NOT_FOUND_ERROR);
        }
        return product;
    }

    public void updateProfile(long branchId, BranchProfileUpdateDto updateDto) {
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

    /*
        지점 계정 소프트 삭제
     */
    public void softDelete(long branchId) {
        try {
            BranchAccount branchAccount = getBranchAccount(branchId);
            try  {
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

    /*
        지점 계정 하드 삭제
     */
    public void hardDelete(long branchId) {
        try {
            productRepository.deleteAllByBranchId(branchId);
            branchProfileRepository.deleteById(branchId);
            branchAccountRepository.deleteById(branchId);
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    /*
    지점 계정에서 상태 설정 가능한 상품 목록 조회
     */
    public List<Product> findOverridableProducts(long branchId) {
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


    /*
    지점 상품 숨김/품절 처리
     */
    private void suspendSale(long branchId, long productId, ProductSalesOverrideStatus overrideStatus) {
        try {
            ProductSalesOverride salesOverride = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId);
            try {
                if (salesOverride == null) {
                    salesOverride = ProductSalesOverride.create(productId, branchId, overrideStatus);
                }
                else if (overrideStatus != salesOverride.getStatus()) {
                    salesOverride.updateStatus(overrideStatus);
                }
                else return;
                productSalesOverrideRepository.save(salesOverride);
            } catch (BranchException e) {
                throw BranchBusinessException.with(BranchErrors.INVALID_BRANCH_ERROR);
            }
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    /*
    지점 상품 숨김 처리
     */
    public void hideSale(long branchId, long productId) {
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

    /*
    지점 상품 품절 처리
     */
    public void soldOut(long branchId, long productId) {
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

    /*
    지점 상품 숨김/품절 해제
     */
    public void restoreSale(long branchId, long productId) {
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
