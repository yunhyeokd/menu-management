package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.auth.SessionInvalidationPort;
import com.dozycoffee.application.branch.dto.BranchAuthKeyReissueResult;
import com.dozycoffee.application.branch.dto.BranchCreateResult;
import com.dozycoffee.application.branch.dto.BranchProfileUpdateDto;
import com.dozycoffee.application.branch.repository.BranchAccountRepository;
import com.dozycoffee.application.branch.repository.BranchProfileRepository;
import com.dozycoffee.application.branch.repository.ProductSalesOverrideRepository;
import com.dozycoffee.application.common.IdentifierGenerator;
import com.dozycoffee.application.auth.PasswordHasher;
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
    private final IdentifierGenerator<BranchId> idGenerator;
    private final BranchCodeGenerator codeGenerator;
    private final BranchAuthKeyGenerator authKeyGenerator;
    private final PasswordHasher passwordHasher;
    private final SessionInvalidationPort sessionInvalidationPort;

    public BranchService(
            BranchAccountRepository branchAccountRepository,
            BranchProfileRepository branchProfileRepository,
            ProductRepository productRepository,
            ProductSalesOverrideRepository productSalesOverrideRepository,
            IdentifierGenerator<BranchId> idGenerator,
            BranchCodeGenerator codeGenerator,
            BranchAuthKeyGenerator authKeyGenerator,
            PasswordHasher passwordHasher,
            SessionInvalidationPort sessionInvalidationPort
    ) {
        this.branchAccountRepository = branchAccountRepository;
        this.branchProfileRepository = branchProfileRepository;
        this.productRepository = productRepository;
        this.productSalesOverrideRepository = productSalesOverrideRepository;
        this.idGenerator = idGenerator;
        this.codeGenerator = codeGenerator;
        this.authKeyGenerator = authKeyGenerator;
        this.passwordHasher = passwordHasher;
        this.sessionInvalidationPort = sessionInvalidationPort;
    }

    public BranchCreateResult create(String name, String address) {
        try {
            branchProfileRepository
                    .findByName(name)
                    .ifPresent(branchProfile -> {
                        throw BranchBusinessException.with(BranchErrors.DUPLICATE_NAME_ERROR);
                    });
            try {
                BranchId branchId = idGenerator.generate();
                BranchCode branchCode = codeGenerator.generate();
                String rawAuthKey = authKeyGenerator.generate();
                String authKeyHash = passwordHasher.hash(rawAuthKey);
                BranchAccount account = BranchAccount.create(branchId, branchCode, authKeyHash);
                BranchProfile profile = BranchProfile.create(branchId, name, address);
                branchAccountRepository.save(account);
                branchProfileRepository.save(profile);
                return new BranchCreateResult(
                        account.getId(),
                        account.getCode(),
                        rawAuthKey,
                        profile.getName(),
                        profile.getAddress(),
                        account.getCreatedAt()
                );
            } catch (BranchException e) {
                throw BranchBusinessException.with(BranchErrors.INVALID_BRANCH_ERROR);
            }
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }

    private void assertBranchAccountExists(BranchId branchId) throws RepositoryException {
        branchAccountRepository.findById(branchId)
                .orElseThrow(() -> BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    private void assertProductExists(ProductId productId) throws RepositoryException {
        productRepository.findById(productId)
                .orElseThrow(() -> BranchBusinessException.with(BranchErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    private BranchAccount getBranchAccount(BranchId branchId) {
        return branchAccountRepository.findById(branchId)
                .orElseThrow(() -> BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    private BranchProfile getBranchProfile(BranchId branchId) throws RepositoryException {
        return branchProfileRepository.findById(branchId)
                .orElseThrow(() -> BranchBusinessException.with(BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    private Product getProduct(ProductId productId) throws RepositoryException {
        return productRepository.findById(productId)
                .orElseThrow(() -> BranchBusinessException.with(BranchErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    public BranchAuthKeyReissueResult reissueAuthKey(BranchId branchId) {
        try {
            BranchAccount account = getBranchAccount(branchId);
            try {
                String rawAuthKey = authKeyGenerator.generate();
                String newHash = passwordHasher.hash(rawAuthKey);
                account.reissueAuthKey(newHash);
                branchAccountRepository.save(account);
                sessionInvalidationPort.invalidate(account);
                return new BranchAuthKeyReissueResult(account.getId(), account.getCode(), rawAuthKey);
            } catch (BranchException e) {
                throw BranchBusinessException.with(BranchErrors.ALREADY_DELETED_ERROR);
            }
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
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
            if (productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId) == null) {
                throw BranchBusinessException.with(BranchErrors.SALES_OVERRIDE_NOT_FOUND_ERROR);
            }
            productSalesOverrideRepository.deleteByBranchIdAndProductId(branchId, productId);
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }
    }
}
