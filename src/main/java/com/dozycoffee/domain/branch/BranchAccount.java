package com.dozycoffee.domain.branch;



import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class BranchAccount {

    private Long id;
    private String code;
    private String authKeyHash;
    private BranchStatus status;
    private Instant createdAt;
    private Instant deletedAt;


    private BranchAccount(Long id, String code, String authKeyHash, BranchStatus status, Instant createdAt, Instant deletedAt) {
        validateRequiredFields(code, authKeyHash, status, createdAt);
        this.id = id;
        this.code = code;
        this.authKeyHash = authKeyHash;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BranchAccount branchAccount)) return false;
        if (id == null || branchAccount.id == null) return false;
        return id.equals(branchAccount.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static BranchAccount of(long id, String code, String authKeyHash, BranchStatus status, Instant createdAt, Instant deletedAt) {
        return new BranchAccount(id, code, authKeyHash, status, createdAt, deletedAt);
    }

    public static BranchAccount create(String code, String authKeyHash) {
        validateCode(code);
        validateAuthKeyHash(authKeyHash);
        return new BranchAccount(null, code, authKeyHash, BranchStatus.ACTIVE, Instant.now(), null);
    }

    private static void validateRequiredFields(String code, String authKeyHash, BranchStatus status, Instant createdAt) {
        if (code == null) throw new BranchException("code cannot be null");
        if (authKeyHash == null) throw new BranchException("authKeyHash cannot be null");
        if (status == null) throw new BranchException("status cannot be null");
        if (createdAt == null) throw new BranchException("createdAt cannot be null");
    }

    private static final Pattern CODE_PATTERN =
            Pattern.compile("^[0-9]{8}$");

    private static void validateCode(String code) {
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            throw new BranchException("Invalid branch code");
        }
    }

    private static void validateAuthKeyHash(String authKeyHash) {
        if (authKeyHash == null || authKeyHash.isBlank()) {
            throw new BranchException("Invalid branch authKeyHash");
        }
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getAuthKeyHash() {
        return authKeyHash;
    }

    public BranchStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
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
    }
}
