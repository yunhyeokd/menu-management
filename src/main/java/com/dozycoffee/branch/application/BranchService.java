package com.dozycoffee.branch.application;

import com.dozycoffee.auth.application.PasswordHasher;
import com.dozycoffee.auth.application.SessionInvalidationPort;
import com.dozycoffee.auth.domain.Credential;
import com.dozycoffee.branch.application.dto.BranchAuthKeyReissueResult;
import com.dozycoffee.branch.application.dto.BranchCreateResult;
import com.dozycoffee.branch.application.dto.BranchProfileUpdateCommand;
import com.dozycoffee.branch.domain.*;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.core.application.exception.*;
import com.dozycoffee.core.domain.IdentifierGenerator;

public class BranchService {

    private final BranchAccountRepository branchAccountRepository;
    private final BranchProfileRepository branchProfileRepository;
    private final BranchProductLifecyclePort productLifecyclePort;
    private final IdentifierGenerator<BranchId> idGenerator;
    private final BranchCodeGenerator codeGenerator;
    private final BranchAuthKeyGenerator authKeyGenerator;
    private final PasswordHasher passwordHasher;
    private final SessionInvalidationPort sessionInvalidationPort;

    public BranchService(
            BranchAccountRepository branchAccountRepository,
            BranchProfileRepository branchProfileRepository,
            BranchProductLifecyclePort productLifecyclePort,
            IdentifierGenerator<BranchId> idGenerator,
            BranchCodeGenerator codeGenerator,
            BranchAuthKeyGenerator authKeyGenerator,
            PasswordHasher passwordHasher,
            SessionInvalidationPort sessionInvalidationPort
    ) {
        this.branchAccountRepository = branchAccountRepository;
        this.branchProfileRepository = branchProfileRepository;
        this.productLifecyclePort = productLifecyclePort;
        this.idGenerator = idGenerator;
        this.codeGenerator = codeGenerator;
        this.authKeyGenerator = authKeyGenerator;
        this.passwordHasher = passwordHasher;
        this.sessionInvalidationPort = sessionInvalidationPort;
    }

    private BranchAccount getBranchAccount(BranchId branchId) {
        return branchAccountRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException(BranchServiceCode.BRN, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    private BranchProfile getBranchProfile(BranchId branchId) throws RepositoryException {
        return branchProfileRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException(BranchServiceCode.BRN, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    public BranchCreateResult create(String name, String address) {
        try {
            branchProfileRepository
                    .findByName(name)
                    .ifPresent(branchProfile -> {
                        throw new ConflictException(BranchServiceCode.BRN, BranchErrors.DUPLICATE_NAME_ERROR);
                    });
            BranchId branchId = idGenerator.generate();
            BranchCode branchCode = codeGenerator.generate();
            Credential rawAuthKey = authKeyGenerator.generate();
            String authKeyHash = passwordHasher.hash(rawAuthKey.getValue());
            BranchAccount account = BranchAccount.create(branchId, branchCode, authKeyHash);
            BranchProfile profile = BranchProfile.create(branchId, name, address);
            branchAccountRepository.save(account);
            branchProfileRepository.save(profile);
            return new BranchCreateResult(
                    account.getId(),
                    account.getCode(),
                    rawAuthKey.getValue(),
                    profile.getName(),
                    profile.getAddress(),
                    account.getCreatedAt()
            );
        } catch (BranchException e) {
            throw new ValidationException(BranchServiceCode.BRN, BranchErrors.INVALID_BRANCH_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    public BranchAuthKeyReissueResult reissueAuthKey(BranchId branchId) {
        try {
            BranchAccount account = getBranchAccount(branchId);
            Credential rawAuthKey = authKeyGenerator.generate();
            String newHash = passwordHasher.hash(rawAuthKey.getValue());
            account.reissueAuthKey(newHash);
            branchAccountRepository.save(account);
            sessionInvalidationPort.invalidate(account);
            return new BranchAuthKeyReissueResult(account.getId(), account.getCode(), rawAuthKey.getValue());
        } catch (BranchException e) {
            throw new ConflictException(BranchServiceCode.BRN, BranchErrors.ALREADY_DELETED_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    public void updateProfile(BranchId branchId, BranchProfileUpdateCommand command) {
        try {
            BranchProfile branchProfile = getBranchProfile(branchId);
            branchProfile.changeName(command.name());
            branchProfile.changeAddress(command.address());
            branchProfileRepository.save(branchProfile);
        } catch (BranchException e) {
            throw new ValidationException(BranchServiceCode.BRN, BranchErrors.INVALID_PROFILE_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    public void softDelete(BranchId branchId) {
        try {
            BranchAccount branchAccount = getBranchAccount(branchId);
            productLifecyclePort.deactivateAllByBranchId(branchId);
            branchAccount.softDelete();
            branchAccountRepository.save(branchAccount);
            sessionInvalidationPort.invalidate(branchAccount);
        } catch (BranchException e) {
            throw new ConflictException(BranchServiceCode.BRN, BranchErrors.ALREADY_DELETED_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    public void hardDelete(BranchId branchId) {
        try {
            BranchAccount branchAccount = getBranchAccount(branchId);
            if (!branchAccount.isSoftDeleted()) {
                throw new ConflictException(BranchServiceCode.BRN, BranchErrors.INVALID_BRANCH_ERROR);
            }
            productLifecyclePort.deleteAllByBranchId(branchId);
            branchProfileRepository.deleteById(branchId);
            branchAccountRepository.deleteById(branchId);
            sessionInvalidationPort.invalidate(branchAccount);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }
}
