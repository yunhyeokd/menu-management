package com.dozycoffee.admin.application.service;

import com.dozycoffee.admin.application.dto.*;
import com.dozycoffee.admin.domain.*;
import com.dozycoffee.admin.application.repository.AdminAccountRepository;
import com.dozycoffee.admin.application.repository.AdminProfileRepository;
import com.dozycoffee.auth.application.PasswordHasher;
import com.dozycoffee.auth.application.SessionInvalidationPort;
import com.dozycoffee.core.domain.IdentifierGenerator;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.admin.application.AdminServiceCode;
import com.dozycoffee.core.application.exception.*;

import java.util.Optional;

public class AdminService {

    private final AdminAccountRepository adminAccountRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final IdentifierGenerator<AdminId> idGenerator;
    private final PasswordHasher passwordHasher;
    private final SessionInvalidationPort sessionInvalidationPort;

    public AdminService(
            AdminAccountRepository adminAccountRepository,
            AdminProfileRepository adminProfileRepository,
            IdentifierGenerator<AdminId> idGenerator,
            PasswordHasher passwordHasher,
            SessionInvalidationPort sessionInvalidationPort
    ) {
        this.adminAccountRepository = adminAccountRepository;
        this.adminProfileRepository = adminProfileRepository;
        this.idGenerator = idGenerator;
        this.passwordHasher = passwordHasher;
        this.sessionInvalidationPort = sessionInvalidationPort;
    }

    private AdminAccount getAdminAccount(AdminId adminId) {
        Optional<AdminAccount> adminAccount = adminAccountRepository.findById(adminId);
        return adminAccount.orElseThrow(() -> new ResourceNotFoundException(AdminServiceCode.ADM, AdminErrors.ADMIN_NOT_FOUND));
    }

    private AdminProfile getAdminProfile(AdminId adminId) {
        return adminProfileRepository.findById(adminId).orElseThrow(() -> new ResourceNotFoundException(AdminServiceCode.ADM, AdminErrors.ADMIN_NOT_FOUND));
    }

    /*
    시스템 관리자 계정을 생성
    * 이미 시스템 관리자 계정이 존재 시 예외 발생
     */
    public SystemAdminRegisterResult registerSystem(SystemAdminRegisterCommand command) {
        try {
            adminAccountRepository.findByAdminRole(AdminRole.SYSTEM)
                    .ifPresent(adminAccount -> {
                        throw new ConflictException(AdminServiceCode.ADM, AdminErrors.DUPLICATE_ACCOUNT_ERROR);
                    });

            AdminId adminId = idGenerator.generate();
            String passwordHash = passwordHasher.hash(command.password());
            AdminAccount adminAccount = AdminAccount.create(
                    adminId,
                    AdminRole.SYSTEM,
                    command.username(),
                    passwordHash
            );
            adminAccountRepository.save(adminAccount);
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

    /*
    사원 관리자 계정을 생성
    !! 같은 username의 관리자 계정 존재 시 예외 발생
     */
    public AdminRegisterResult registerStaff(AdminRegisterCommand command) {
        try {
            adminAccountRepository.findByUsername(command.username())
                    .ifPresent(adminAccount -> {
                        throw new ConflictException(AdminServiceCode.ADM, AdminErrors.DUPLICATE_ACCOUNT_ERROR);
                    });
            AdminId adminId = idGenerator.generate();
            String passwordHash = passwordHasher.hash(command.password());
            AdminAccount newAdminAccount = AdminAccount.create(adminId, AdminRole.STAFF, command.username(), passwordHash);
            AdminProfile newAdminProfile = AdminProfile.create(
                    adminId,
                    command.employeeNo(),
                    command.name(),
                    command.phone(),
                    command.email()
            );
            adminAccountRepository.save(newAdminAccount);
            adminProfileRepository.save(newAdminProfile);
            return new AdminRegisterResult(
                    newAdminAccount.getId(),
                    newAdminAccount.getAdminRole(),
                    newAdminAccount.getUsername(),
                    newAdminProfile.getName(),
                    newAdminProfile.getEmail(),
                    newAdminAccount.getCreatedAt()
            );
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    /*
    계정 생성 요청을 승인
    * 계정이 활성화 상태가 되어 로그인 가능
    !! PENDING 상태가 아닌 경우 예외 발생
     */
    public void approve(AdminId adminId) {
        try {
            AdminAccount adminAccount = getAdminAccount(adminId);
            adminAccount.approve();
            adminAccountRepository.save(adminAccount);
        } catch (AdminException e) {
            throw new ConflictException(AdminServiceCode.ADM, AdminErrors.UNABLE_APPROVAL_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    /*
    계정 생성 요청을 거부
    * 같은 계정에 대한 생성을 계속 요청하는 상황을 막기 위해 레코드 삭제하지 않고 소프트 삭제 처리됨
    !! PENDING 상태가 아닌 경우 예외 발생
     */
    public void reject(AdminId adminId) {
        try {
            AdminAccount adminAccount = getAdminAccount(adminId);
            adminAccount.reject();
            adminAccountRepository.save(adminAccount);
        } catch (AdminException e) {
            throw new ConflictException(AdminServiceCode.ADM, AdminErrors.UNABLE_APPROVAL_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    /*
    계정 프로필의 이름, 전화번호, 이메일 변경
    !! 변경 속성값이 도메인의 속성 규칙에 어긋나는 경우 예외 발생
     */
    public AdminProfileUpdateResult updateProfile(AdminId adminId, AdminProfileUpdateCommand command) {
        try {
            AdminProfile adminProfile = getAdminProfile(adminId);
            command.name().ifPresent(adminProfile::changeName);
            command.phone().ifPresent(adminProfile::changePhoneNumber);
            command.email().ifPresent(adminProfile::changeEmail);
            adminProfileRepository.save(adminProfile);
            return new AdminProfileUpdateResult(
                    adminProfile.getAdminId(),
                    adminProfile.getName(),
                    adminProfile.getPhone(),
                    adminProfile.getEmail()
            );
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    /*
    관리자 계정 소프트 삭제
    계정을 비활성화 상태로 변경하고 삭제 일시 기록
    계정 세션 무효화
    !! 시스템 계정 소프트 삭제 요청 시 예외 발생
    !! 이미 소프트 삭제된 계정에 중복 요청 시 예외 발생
     */
    public void softDelete(AdminId adminId) {
        try {
            AdminAccount adminAccount = getAdminAccount(adminId);
            adminAccount.softDelete();
            adminAccountRepository.save(adminAccount);
            sessionInvalidationPort.invalidate(adminAccount);
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    /*
    소프트 삭제가 완료된 관리자 계정 하드 삭제
    계정 세션 무효화 및 해당 계정 레코드 완전 삭제
    * 시스템 계정의 경우 소프트 삭제에서 거부되므로 하드 삭제 불가
    !! 소프트 삭제된 계정이 아닌 경우 예외 발생
     */
    public void hardDelete(AdminId adminId) {
        try {
            AdminAccount adminAccount = getAdminAccount(adminId);
            if (!adminAccount.isSoftDeleted()) {
                throw new ConflictException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
            }
            adminProfileRepository.deleteById(adminId);
            adminAccountRepository.deleteById(adminId);
            sessionInvalidationPort.invalidate(adminAccount);
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

    public void changePassword(AdminId adminId, String currentPassword, String newPassword) {
        try {
            AdminAccount adminAccount = getAdminAccount(adminId);
            if (!passwordHasher.matches(currentPassword, adminAccount.getPasswordHash())) {
                throw new AuthenticationException(AdminServiceCode.ADM, AdminErrors.AUTHENTICATION_FAILED_ERROR);
            }
            String passwordHash = passwordHasher.hash(newPassword);
            adminAccount.updatePasswordHash(passwordHash);
            adminAccountRepository.save(adminAccount);
            sessionInvalidationPort.invalidate(adminAccount);
        } catch (AdminException e) {
            throw new ValidationException(AdminServiceCode.ADM, AdminErrors.INVALID_ADMIN_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(AdminServiceCode.ADM, AdminErrors.UNKNOWN_ERROR);
        }
    }

}
