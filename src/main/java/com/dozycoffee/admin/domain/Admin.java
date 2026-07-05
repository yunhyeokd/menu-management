package com.dozycoffee.admin.domain;

import com.dozycoffee.core.security.Principal;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class Admin extends AdminPrincipal {

    private AdminProfile profile;

    private Admin(
            AdminId id,
            String username,
            String passwordHash,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt,
            AdminProfile profile
    ) {
        super(id, AdminRole.ADMIN, username, passwordHash, status, createdAt, deletedAt);
        if (profile == null) {
            throw new AdminException("admin profile cannot be null");
        }
        this.profile = profile;
    }

    public static Admin of(
            AdminId id,
            String username,
            String password,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt,
            AdminProfile profile
    ) {
        return new Admin(id, username, password, status, createdAt, deletedAt, profile);
    }

    public static Admin create(AdminId id, String username, String password, AdminProfile profile) {
        return new Admin(id, username, password, AdminStatus.PENDING, Instant.now(), null, profile);
    }

    public AdminProfile getProfile() {
        return profile;
    }

    public void updateProfile(String name, String phone, String email) {
        this.profile = AdminProfile.create(profile.getEmployeeNo(), name, phone, email);
    }

    public void updateStatus(AdminStatus status) {
        setStatus(status);
    }

    public void softDelete() {
        if (deletedAt != null) {
            throw new AdminException("admin account is already deleted");
        }
        updateStatus(AdminStatus.INACTIVE);
        deletedAt = Instant.now();
    }

    public void approve() {
        if (status != AdminStatus.PENDING) {
            throw new AdminException("attempt to approve non-pending status");
        }
        updateStatus(AdminStatus.ACTIVE);
    }

    public void reject() {
        if (status != AdminStatus.PENDING) {
            throw new AdminException("attempt to reject non-pending status");
        }
        softDelete();
    }

    public boolean isSoftDeleted() {
        return deletedAt != null;
    }

}
