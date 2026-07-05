package com.dozycoffee.admin.domain;

import com.dozycoffee.core.security.Principal;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class AdminPrincipal implements Principal {

    private final AdminId id;
    private final AdminRole adminRole;
    private final String username;
    private String passwordHash;
    protected AdminStatus status;
    private final Instant createdAt;
    protected Instant deletedAt;

    protected AdminPrincipal(
            AdminId id,
            AdminRole role,
            String username,
            String passwordHash,
            AdminStatus status,
            Instant createdAt,
            Instant deletedAt
    ) {
        if (id == null) throw new AdminException("id cannot be null");
        if (role == null) throw new AdminException("role cannot be null");
        validateUsername(username);
        validatePasswordHash(passwordHash);
        if (status == null) throw new AdminException("status cannot be null");
        if (createdAt == null) throw new AdminException("createdAt cannot be null");
        this.id = id;
        this.adminRole = role;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else if (o instanceof Principal principal) {
            return Objects.equals(getSubject(), principal.getSubject())
                    && Objects.equals(getRole(), principal.getRole());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubject(), getRole());
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

    protected void setStatus(AdminStatus status) {
        if (status == null) throw new AdminException("status cannot be null");
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void updatePasswordHash(String passwordHash) {
        setPasswordHash(passwordHash);
    }

    public boolean isActive() {
        return status == AdminStatus.ACTIVE;
    }
}
