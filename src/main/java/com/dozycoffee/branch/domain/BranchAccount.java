package com.dozycoffee.domain.branch;

import com.dozycoffee.auth.domain.Principal;

import java.time.Instant;
import java.util.Objects;

public class BranchAccount implements Principal {

    public static final String ROLE = "BRANCH";

    private BranchId id;
    private BranchCode code;
    private String authKeyHash;
    private BranchStatus status;
    private Instant createdAt;
    private Instant deletedAt;


    private BranchAccount(BranchId id, BranchCode code, String authKeyHash, BranchStatus status, Instant createdAt, Instant deletedAt) {
        setId(id);
        setCode(code);
        setAuthKeyHash(authKeyHash);
        setStatus(status);
        setCreatedAt(createdAt);
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BranchAccount branchAccount)) return false;
        return id.equals(branchAccount.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static BranchAccount of(BranchId id, BranchCode code, String authKeyHash, BranchStatus status, Instant createdAt, Instant deletedAt) {
        validateDeletedAccountStatus(status, deletedAt);
        return new BranchAccount(id, code, authKeyHash, status, createdAt, deletedAt);
    }

    public static BranchAccount create(BranchId id, BranchCode code, String authKeyHash) {
        return new BranchAccount(id, code, authKeyHash, BranchStatus.ACTIVE, Instant.now(), null);
    }

    private static void validateDeletedAccountStatus(BranchStatus branchStatus, Instant deletedAt) {
        if (deletedAt != null && branchStatus != BranchStatus.INACTIVE) {
            throw new BranchException("Deleted branch account must be inactive");
        }
    }

    @Override
    public String getSubject() {
        return getId().toString();
    }

    @Override
    public String getRole() {
        return ROLE;
    }

    public BranchId getId() {
        return id;
    }

    private void setId(BranchId id) {
        if (id == null) throw new BranchException("id cannot be null");
        this.id = id;
    }

    public BranchCode getCode() {
        return code;
    }

    private void setCode(BranchCode code) {
        if (code == null) {
            throw new BranchException("Invalid branch code");
        }
        this.code = code;
    }

    public String getAuthKeyHash() {
        return authKeyHash;
    }

    private void setAuthKeyHash(String authKeyHash) {
        if (authKeyHash == null || authKeyHash.isBlank()) {
            throw new BranchException("Invalid branch authKeyHash");
        }
        this.authKeyHash = authKeyHash;
    }

    public BranchStatus getStatus() {
        return status;
    }

    private void setStatus(BranchStatus status) {
        if (status == null) throw new BranchException("status cannot be null");
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new BranchException("createdAt cannot be null");
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void activate() {
        this.status = BranchStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = BranchStatus.INACTIVE;
    }

    public void softDelete() {
        if (deletedAt != null) {
            throw new BranchException("Branch is already deleted");
        }
        this.deletedAt = Instant.now();
        deactivate();
    }

    public void reissueAuthKey(String newHash) {
        if (deletedAt != null) {
            throw new BranchException("Branch is already deleted");
        }
        setAuthKeyHash(newHash);
    }

    public boolean isActive() {
        return status == BranchStatus.ACTIVE;
    }

    public boolean isSoftDeleted() {
        return deletedAt != null;
    }
}
