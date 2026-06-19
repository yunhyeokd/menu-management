package com.dozycoffee.domain.admin;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class AdminAccount {

    private AdminId id;
    private AdminRole role;
    private String username;
    private String passwordHash;
    private AdminStatus status;
    private Instant createdAt;
    private Instant deletedAt;

    private AdminAccount(
            AdminId id,
            AdminRole role,
            String username,
            String passwordHash,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt
    ) {
        setId(id);
        setRole(role);
        setUsername(username);
        setPasswordHash(passwordHash);
        setStatus(status);
        setCreatedAt(createdAt);
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AdminAccount adminAccount)) return false;
        return Objects.equals(id, adminAccount.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static AdminAccount of(
            AdminId id,
            AdminRole role,
            String username,
            String password,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt
    ) {
        return new AdminAccount(id, role, username, password, status, createdAt, deletedAt);
    }

    public static AdminAccount create(AdminId id, AdminRole role, String username, String password) {
        AdminStatus status = role == AdminRole.SYSTEM ? AdminStatus.ACTIVE : AdminStatus.PENDING;
        return new AdminAccount(id, role, username, password, status, Instant.now(), null);
    }

    public AdminId getId() {
        return id;
    }

    private void setId(AdminId id) {
        if (id == null) throw new AdminException("id cannot be null");
        this.id = id;
    }

    public AdminRole getRole() {
        return role;
    }

    private void setRole(AdminRole role) {
        if (role == null) throw new AdminException("role cannot be null");
        this.role = role;
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

    private void setUsername(String username) {
        validateUsername(username);
        this.username = username;
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

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new AdminException("createdAt cannot be null");
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void updateStatus(AdminStatus status) {
        if (role == AdminRole.SYSTEM) {
            throw new AdminException("system admin status cannot be updated");
        }
        setStatus(status);
    }

    public void updatePasswordHash(String passwordHash) {
        setPasswordHash(passwordHash);
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
