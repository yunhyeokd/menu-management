package com.dozycoffee.domain.admin;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class AdminAccount {

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-z][a-z0-9_]{3,19}$");

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
        this.id = id;
        this.role = role;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
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
        Objects.requireNonNull(role, "role cannot be null");
        Objects.requireNonNull(username, "username cannot be null");
        Objects.requireNonNull(password,  "password cannot be null");
        Objects.requireNonNull(status,  "status cannot be null");
        Objects.requireNonNull(createdAt,  "createdAt cannot be null");

        return new AdminAccount(id, role, username, password, status, createdAt, deletedAt);
    }

    public static AdminAccount create(AdminRole role, String username, String password) {
        if (role == null) {
            throw new AdminException("role cannot be null");
        }
        validateUsername(username);
        validatePasswordHash(password);
        AdminStatus status = role == AdminRole.SYSTEM ? AdminStatus.ACTIVE : AdminStatus.PENDING;
        return new AdminAccount(null, role, username, password, status, Instant.now(), null);
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
        this.status = status;
    }

    private static void validateUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new AdminException("Invalid username");
        }
    }

    private static void validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new AdminException("Password is required");
        }
    }

}
