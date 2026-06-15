package com.dozycoffee.domain.admin;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class AdminAccount {

    private Long id;
    private AdminRole role;
    private String username;
    private String passwordHash;
    private AdminStatus status;
    private Instant createdAt;
    private Instant deletedAt;

    private AdminAccount(
            Long id,
            AdminRole role,
            String username,
            String passwordHash,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt
    ) {
        validateRequiredFields(role, username, passwordHash, status, createdAt);
        this.id = id;
        this.role = role;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AdminAccount adminAccount)) return false;
        if (id == null || adminAccount.id == null) return false;
        return Objects.equals(id, adminAccount.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static AdminAccount of(
            long id,
            AdminRole role,
            String username,
            String password,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt
    ) {
        return new AdminAccount(id, role, username, password, status, createdAt, deletedAt);
    }

    public static AdminAccount create(AdminRole role, String username, String password) {
        validateUsername(username);
        validatePasswordHash(password);
        AdminStatus status = role == AdminRole.SYSTEM ? AdminStatus.ACTIVE : AdminStatus.PENDING;
        return new AdminAccount(null, role, username, password, status, Instant.now(), null);
    }

    private static void validateRequiredFields(AdminRole role, String username, String passwordHash, AdminStatus status, Instant createdAt) {
        validateRoleAndStatus(role, status);
        if (username == null) throw new AdminException("username cannot be null");
        if (passwordHash == null) throw new AdminException("password cannot be null");
        if (createdAt == null) throw new AdminException("createdAt cannot be null");
    }

    private static void validateRoleAndStatus(AdminRole role, AdminStatus status) {
        if (role == null) throw new AdminException("role cannot be null");
        if (status == null) throw new AdminException("status cannot be null");
        if (role == AdminRole.SYSTEM) {
            if (status != AdminStatus.ACTIVE) throw new AdminException("status must be ACTIVE");
        }
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
            throw new AdminException("username length must be between 3 and 19 characters");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new AdminException("Invalid username");
        }
    }

    private static void validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new AdminException("Password is required");
        }
    }

    public Long getId() {
        return id;
    }

    public AdminRole getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AdminStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void updateStatus(AdminStatus status) {
        if (role == AdminRole.SYSTEM) {
            throw new AdminException("system admin status cannot be updated");
        }
        if (status == null) throw new AdminException("status cannot be null");
        this.status = status;
    }

    public void updatePasswordHash(String passwordHash) {
        validatePasswordHash(passwordHash);
        this.passwordHash = passwordHash;
    }

    public void softDelete() {
        if (role == AdminRole.SYSTEM) {
            throw new AdminException("system admin status cannot be deleted");
        }
        if (deletedAt != null) {
            throw new AdminException("admin account is already deleted");
        }
        deletedAt = Instant.now();
    }


}
