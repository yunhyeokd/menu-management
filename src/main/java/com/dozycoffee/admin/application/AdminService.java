package com.dozycoffee.admin.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.admin.application.dto.*;
import com.dozycoffee.admin.domain.*;
import com.dozycoffee.auth.application.PasswordHasher;
import com.dozycoffee.auth.application.SessionInvalidationPort;
import com.dozycoffee.core.domain.IdentifierGenerator;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.core.application.exception.*;

@Service
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;
    private final IdentifierGenerator<AdminId> idGenerator;
    private final PasswordHasher passwordHasher;
    private final SessionInvalidationPort sessionInvalidationPort;

    public AdminService(
            AdminRepository adminRepository,
            IdentifierGenerator<AdminId> idGenerator,
            PasswordHasher passwordHasher,
            SessionInvalidationPort sessionInvalidationPort
    ) {
        this.adminRepository = adminRepository;
        this.idGenerator = idGenerator;
        this.passwordHasher = passwordHasher;
        this.sessionInvalidationPort = sessionInvalidationPort;
    }

    private Admin getAdmin(AdminId adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException(AdminServiceCode.ADM, AdminErrors.ADMIN_NOT_FOUND));
    }

    public SystemAdminRegisterResult registerSystem(SystemAdminRegisterCommand command) {
        try {
            adminRepository.findByRole(AdminRole.SYSTEM)
                    .ifPresent(a -> {
                        throw new ConflictException(AdminServiceCode.ADM, AdminErrors.DUPLICATE_ACCOUNT_ERROR);
                    });
            AdminId adminId = idGenerator.generate();
            String passwordHash = passwordHasher.hash(command.password());
            Admin adminAccount = Admin.create(adminId, AdminRole.SYSTEM, command.username(), passwordHash, null);
            adminRepository.save(adminAccount);
            return new SystemAdminRegisterResult(
                    adminAccount.getId(),
                    adminAccount.getAdminRole(),
                    adminAccount.getUsername(),
                    adminAccount.getCreatedAt()
            );
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public AdminRegisterResult registerStaff(AdminRegisterCommand command) {
        try {
            adminRepository.findByUsername(command.username())
                    .ifPresent(a -> {
                        throw new ConflictException(AdminServiceCode.ADM, AdminErrors.DUPLICATE_ACCOUNT_ERROR);
                    });
            if (adminRepository.existsByEmployeeNo(command.employeeNo())) {
                throw new ConflictException(AdminServiceCode.ADM, AdminErrors.DUPLICATE_EMPLOYEE_NO_ERROR);
            }

            AdminId adminId = idGenerator.generate();
            String passwordHash = passwordHasher.hash(command.password());
            AdminProfile profile = AdminProfile.create(
                    command.employeeNo(),
                    command.name(),
                    command.phone(),
                    command.email()
            );
            Admin account = Admin.create(adminId, AdminRole.STAFF, command.username(), passwordHash, profile);
            adminRepository.save(account);
            return new AdminRegisterResult(
                    account.getId(),
                    account.getAdminRole(),
                    account.getUsername(),
                    profile.getName(),
                    profile.getEmail(),
                    account.getCreatedAt()
            );
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void approve(AdminId adminId) {
        try {
            Admin adminAccount = getAdmin(adminId);
            adminAccount.approve();
            adminRepository.save(adminAccount);
        } catch (AdminException e) {
            throw new ConflictException(AdminServiceCode.ADM, AdminErrors.UNABLE_APPROVAL_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void reject(AdminId adminId) {
        try {
            Admin adminAccount = getAdmin(adminId);
            adminAccount.reject();
            adminRepository.save(adminAccount);
        } catch (AdminException e) {
            throw new ConflictException(AdminServiceCode.ADM, AdminErrors.UNABLE_APPROVAL_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public AdminProfileUpdateResult updateProfile(AdminId adminId, AdminProfileUpdateCommand command) {
        try {
            Admin account = getAdmin(adminId);
            account.updateProfile(command.name(), command.phone(), command.email());
            adminRepository.save(account);
            AdminProfile profile = account.getProfile();
            return new AdminProfileUpdateResult(
                    account.getId(),
                    profile.getName(),
                    profile.getPhone(),
                    profile.getEmail()
            );
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void softDelete(AdminId adminId) {
        try {
            Admin adminAccount = getAdmin(adminId);
            adminAccount.softDelete();
            adminRepository.save(adminAccount);
            sessionInvalidationPort.invalidate(adminAccount);
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void hardDelete(AdminId adminId) {
        try {
            Admin adminAccount = getAdmin(adminId);
            if (!adminAccount.isSoftDeleted()) {
                throw new ConflictException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
            }
            adminRepository.deleteById(adminId);
            sessionInvalidationPort.invalidate(adminAccount);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void changePassword(AdminId adminId, String currentPassword, String newPassword) {
        try {
            Admin adminAccount = getAdmin(adminId);
            if (!passwordHasher.matches(currentPassword, adminAccount.getPasswordHash())) {
                throw new AuthenticationException(AdminServiceCode.ADM, AdminErrors.AUTHENTICATION_FAILED_ERROR);
            }
            String passwordHash = passwordHasher.hash(newPassword);
            adminAccount.updatePasswordHash(passwordHash);
            adminRepository.save(adminAccount);
            sessionInvalidationPort.invalidate(adminAccount);
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }
}
