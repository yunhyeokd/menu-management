package com.dozycoffee.branch.application;

import com.dozycoffee.branch.application.dto.BranchCreateCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.core.security.PasswordHasher;
import com.dozycoffee.core.security.SessionInvalidationPort;
import com.dozycoffee.core.security.Credential;
import com.dozycoffee.branch.application.dto.BranchAuthKeyReissueResult;
import com.dozycoffee.branch.application.dto.BranchCreateResult;
import com.dozycoffee.branch.application.dto.BranchProfileUpdateCommand;
import com.dozycoffee.branch.domain.*;
import com.dozycoffee.core.exception.RepositoryException;
import com.dozycoffee.core.exception.*;
import com.dozycoffee.core.id.IdentifierGenerator;

import java.util.List;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchProductLifecyclePort productLifecyclePort;
    private final IdentifierGenerator<BranchId> idGenerator;
    private final BranchCodeGenerator codeGenerator;
    private final BranchAuthKeyGenerator authKeyGenerator;
    private final PasswordHasher passwordHasher;
    private final SessionInvalidationPort sessionInvalidationPort;

    public BranchService(
            BranchRepository branchRepository,
            BranchProductLifecyclePort productLifecyclePort,
            IdentifierGenerator<BranchId> idGenerator,
            BranchCodeGenerator codeGenerator,
            BranchAuthKeyGenerator authKeyGenerator,
            PasswordHasher passwordHasher,
            SessionInvalidationPort sessionInvalidationPort
    ) {
        this.branchRepository = branchRepository;
        this.productLifecyclePort = productLifecyclePort;
        this.idGenerator = idGenerator;
        this.codeGenerator = codeGenerator;
        this.authKeyGenerator = authKeyGenerator;
        this.passwordHasher = passwordHasher;
        this.sessionInvalidationPort = sessionInvalidationPort;
    }

    private Branch getBranch(BranchId branchId) throws RepositoryException {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException(BranchServiceCode.BRN, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Transactional(readOnly = true)
    public Branch findById(BranchId branchId) {
        try {
            return getBranch(branchId);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<Branch> findAll() {
        try {
            return branchRepository.findAll();
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public BranchCreateResult create(BranchCreateCommand command) {
        try {
            branchRepository.findByName(command.name())
                    .ifPresent(existing -> {
                        throw new ConflictException(BranchServiceCode.BRN, BranchErrors.DUPLICATE_NAME_ERROR);
                    });
            BranchId branchId = idGenerator.generate();
            BranchCode branchCode = codeGenerator.generate();
            Credential rawAuthKey = authKeyGenerator.generate();
            String authKeyHash = passwordHasher.hash(rawAuthKey.getValue());
            Branch branch = Branch.create(branchId, branchCode, authKeyHash, command.name(), command.address());
            branchRepository.save(branch);
            return new BranchCreateResult(
                    branch.getId(),
                    branch.getCode(),
                    rawAuthKey.getValue(),
                    branch.getName(),
                    branch.getAddress(),
                    branch.getCreatedAt()
            );
        } catch (BranchException e) {
            throw new ValidationException(BranchServiceCode.BRN, BranchErrors.INVALID_BRANCH_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public BranchAuthKeyReissueResult reissueAuthKey(BranchId branchId) {
        try {
            Branch branch = getBranch(branchId);
            Credential rawAuthKey = authKeyGenerator.generate();
            String newHash = passwordHasher.hash(rawAuthKey.getValue());
            branch.reissueAuthKey(newHash);
            branchRepository.save(branch);
            sessionInvalidationPort.invalidate(branch);
            return new BranchAuthKeyReissueResult(branch.getId(), branch.getCode(), rawAuthKey.getValue());
        } catch (BranchException e) {
            throw new ConflictException(BranchServiceCode.BRN, BranchErrors.ALREADY_DELETED_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void updateProfile(BranchId branchId, BranchProfileUpdateCommand command) {
        try {
            Branch branch = getBranch(branchId);
            branchRepository.findByName(command.name())
                            .ifPresent(existing -> {
                                throw new ConflictException(BranchServiceCode.BRN, BranchErrors.DUPLICATE_NAME_ERROR);
                            });
            branch.changeName(command.name());
            branch.changeAddress(command.address());
            branchRepository.save(branch);
        } catch (BranchException e) {
            throw new ValidationException(BranchServiceCode.BRN, BranchErrors.INVALID_PROFILE_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void softDelete(BranchId branchId) {
        try {
            Branch branch = getBranch(branchId);
            productLifecyclePort.deactivateAllByBranchId(branchId);
            branch.softDelete();
            branchRepository.save(branch);
            sessionInvalidationPort.invalidate(branch);
        } catch (BranchException e) {
            throw new ConflictException(BranchServiceCode.BRN, BranchErrors.ALREADY_DELETED_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void hardDelete(BranchId branchId) {
        try {
            Branch branch = getBranch(branchId);
            if (!branch.isSoftDeleted()) {
                throw new ConflictException(BranchServiceCode.BRN, BranchErrors.INVALID_BRANCH_ERROR);
            }
            productLifecyclePort.deleteAllByBranchId(branchId);
            branchRepository.deleteById(branchId);
            sessionInvalidationPort.invalidate(branch);
        } catch (RepositoryException e) {
            throw new SystemException(BranchServiceCode.BRN, BranchErrors.UNKNOWN_ERROR);
        }
    }
}
