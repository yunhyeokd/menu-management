package com.dozycoffee.admin.domain;

import com.dozycoffee.auth.domain.Principal;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class Admin implements Principal {

    private final AdminId id;
    private final AdminRole adminRole;
    private final String username;
    private String passwordHash;
    private AdminStatus status;
    private final Instant createdAt;
    private Instant deletedAt;
    private AdminProfile profile;

    private Admin(
            AdminId id,
            AdminRole role,
            String username,
            String passwordHash,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt,
            AdminProfile profile
    ) {
        if (id == null) throw new AdminException("id cannot be null");
        if (role == null) throw new AdminException("role cannot be null");
        validateUsername(username);
        validatePasswordHash(passwordHash);
        if (status == null) throw new AdminException("status cannot be null");
        if (createdAt == null) throw new AdminException("createdAt cannot be null");
        if (role != AdminRole.SYSTEM && profile == null)
            throw new AdminException("profile is required for non-SYSTEM admin");
        this.id = id;
        this.adminRole = role;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Admin adminAccount)) return false;
        return Objects.equals(id, adminAccount.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static Admin of(
            AdminId id,
            AdminRole role,
            String username,
            String password,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt,
            AdminProfile profile
    ) {
        return new Admin(id, role, username, password, status, createdAt, deletedAt, profile);
    }

    public static Admin create(AdminId id, AdminRole role, String username, String password, AdminProfile profile) {
        AdminStatus status = role == AdminRole.SYSTEM ? AdminStatus.ACTIVE : AdminStatus.PENDING;
        return new Admin(id, role, username, password, status, Instant.now(), null, profile);
    }

    public AdminId getId() {
        return id;
    }

    @Override
    public String getSubject() {
        return getId().toString();
    }

    @Override
    public String getRole() {
        return adminRole.name();
    }

    public AdminRole getAdminRole() {
        return adminRole;
    }

    public String getUsername() {
        return username;
    }

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-z][a-z0-9_]+$");

    private static final int USERNAME_MIN_LENGTH = 4;
    private static final int USERNAME_MAX_LENGTH = 20;

    private static void validateUsername(String username) {
        if (username == null) {
            throw new AdminException("username cannot be null");
        }
        if (username.length() < USERNAME_MIN_LENGTH || username.length() > USERNAME_MAX_LENGTH) {
            throw new AdminException("username length must be between 4 and 20 characters");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new AdminException("Invalid username");
        }
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    private static void validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new AdminException("Password is required");
        }
    }

    private void setPasswordHash(String passwordHash) {
        validatePasswordHash(passwordHash);
        this.passwordHash = passwordHash;
    }

    public AdminStatus getStatus() {
        return status;
    }

    private void setStatus(AdminStatus status) {
        if (status == null) throw new AdminException("status cannot be null");
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public AdminProfile getProfile() {
        return profile;
    }

    public void updateProfile(String name, String phone, String email) {
        if (profile == null) throw new AdminException("cannot update profile on SYSTEM admin");
        this.profile = AdminProfile.create(
                profile.getEmployeeNo(),
                name != null ? name : profile.getName(),
                phone != null ? phone : profile.getPhone(),
                email != null ? email : profile.getEmail()
        );
    }

    public void updateStatus(AdminStatus status) {
        if (adminRole == AdminRole.SYSTEM) {
            throw new AdminException("system admin status cannot be updated");
        }
        setStatus(status);
    }

    public void updatePasswordHash(String passwordHash) {
        setPasswordHash(passwordHash);
    }

    public void softDelete() {
        if (adminRole == AdminRole.SYSTEM) {
            throw new AdminException("system admin status cannot be deleted");
        }
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

    public boolean isActive() {
        return status == AdminStatus.ACTIVE;
    }
}
