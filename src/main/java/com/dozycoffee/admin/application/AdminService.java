package com.dozycoffee.admin.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dozycoffee.admin.application.dto.*;
import com.dozycoffee.admin.domain.*;
import com.dozycoffee.core.security.PasswordHasher;
import com.dozycoffee.core.security.SessionInvalidationPort;
import com.dozycoffee.core.id.IdentifierGenerator;
import com.dozycoffee.core.exception.base.*;
import com.dozycoffee.core.exception.service.*;

import java.util.List;

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

    private AdminPrincipal getAdminPrincipal(AdminId adminId) throws RepositoryException {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException(AdminServiceCode.ADM, AdminErrors.ADMIN_NOT_FOUND));
    }

    private Admin getAdmin(AdminId adminId) {
        return adminRepository.findAdminById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException(AdminServiceCode.ADM, AdminErrors.ADMIN_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<AdminPrincipal> findAll() {
        try {
            return adminRepository.findAll();
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Admin findById(AdminId adminId) {
        try {
            return getAdmin(adminId);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public SystemAdmin registerSystem(SystemAdminRegisterCommand command) {
        try {
            adminRepository.findSystemAdmin().ifPresent(a -> {
                        throw new ConflictException(AdminServiceCode.ADM, AdminErrors.DUPLICATE_ACCOUNT_ERROR);
                    });
            AdminId adminId = idGenerator.generate();
            String passwordHash = passwordHasher.hash(command.password());
            SystemAdmin admin = SystemAdmin.create(adminId, command.username(), passwordHash);
            adminRepository.save(admin);
            return admin;
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public Admin registerAdmin(AdminRegisterCommand command) {
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
            Admin admin = Admin.create(adminId, command.username(), passwordHash, profile);
            adminRepository.save(admin);
            return admin;
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void approve(AdminId adminId) {
        try {
            Admin admin = getAdmin(adminId);
            admin.approve();
            adminRepository.save(admin);
        } catch (AdminException e) {
            throw new ConflictException(AdminServiceCode.ADM, AdminErrors.UNABLE_APPROVAL_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void reject(AdminId adminId) {
        try {
            Admin admin = getAdmin(adminId);
            admin.reject();
            adminRepository.save(admin);
        } catch (AdminException e) {
            throw new ConflictException(AdminServiceCode.ADM, AdminErrors.UNABLE_APPROVAL_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public Admin updateProfile(AdminId adminId, AdminProfileUpdateCommand command) {
        try {
            Admin admin = getAdmin(adminId);
            admin.updateProfile(command.name(), command.phone(), command.email());
            adminRepository.save(admin);
            return admin;
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void softDelete(AdminId adminId) {
        try {
            Admin admin = getAdmin(adminId);
            admin.softDelete();
            adminRepository.save(admin);
            sessionInvalidationPort.invalidate(admin);
        } catch (AdminException e) {
            throw new ConflictException(AdminServiceCode.ADM, AdminErrors.ALREADY_DELETED_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void hardDelete(AdminId adminId) {
        try {
            Admin admin = getAdmin(adminId);
            if (!admin.isSoftDeleted()) {
                throw new ConflictException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
            }
            adminRepository.deleteById(adminId);
            sessionInvalidationPort.invalidate(admin);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void changePassword(AdminId adminId, String currentPassword, String newPassword) {
        try {
            Admin admin = getAdmin(adminId);
            if (!passwordHasher.matches(currentPassword, admin.getPasswordHash())) {
                throw new AuthenticationException(AdminServiceCode.ADM, AdminErrors.AUTHENTICATION_FAILED_ERROR);
            }
            String passwordHash = passwordHasher.hash(newPassword);
            admin.updatePasswordHash(passwordHash);
            adminRepository.save(admin);
            sessionInvalidationPort.invalidate(admin);
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }
}
