package com.dozycoffee.branch.domain;

import com.dozycoffee.auth.domain.Principal;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class Branch implements Principal {

    public static final String ROLE = "BRANCH";

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ \\-()&.]+$");
    private static final Pattern NAME_LETTER_PATTERN =
            Pattern.compile("[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]");
    private static final int NAME_MAX_LENGTH = 30;
    private static final int ADDRESS_MAX_LENGTH = 255;

    private final BranchId id;
    private final BranchCode code;
    private String authKeyHash;
    private BranchStatus status;
    private final Instant createdAt;
    private Instant deletedAt;
    private String name;
    private String address;

    private Branch(BranchId id, BranchCode code, String authKeyHash, BranchStatus status, Instant createdAt, Instant deletedAt, String name, String address) {
        if (id == null) throw new BranchException("id cannot be null");
        if (code == null) throw new BranchException("Invalid branch code");
        if (authKeyHash == null || authKeyHash.isBlank()) throw new BranchException("Invalid branch authKeyHash");
        if (status == null) throw new BranchException("status cannot be null");
        if (createdAt == null) throw new BranchException("createdAt cannot be null");
        this.id = id;
        this.code = code;
        this.authKeyHash = authKeyHash;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        setName(name);
        setAddress(address);
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

    public static Branch of(BranchId id, BranchCode code, String authKeyHash, BranchStatus status, Instant createdAt, Instant deletedAt, String name, String address) {
        validateDeletedStatus(status, deletedAt);
        return new Branch(id, code, authKeyHash, status, createdAt, deletedAt, name, address);
    }

    public static Branch create(BranchId id, BranchCode code, String authKeyHash, String name, String address) {
        return new Branch(id, code, authKeyHash, BranchStatus.ACTIVE, Instant.now(), null, name, address);
    }

    private static void validateDeletedStatus(BranchStatus status, Instant deletedAt) {
        if (deletedAt != null && status != BranchStatus.INACTIVE) {
            throw new BranchException("Deleted branch must be inactive");
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

    public BranchCode getCode() {
        return code;
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

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        if (name == null || name.isBlank()) throw new BranchException("Invalid branch name");
        if (!name.equals(name.strip())) throw new BranchException("Branch name must not have leading or trailing whitespace");
        if (name.length() > NAME_MAX_LENGTH) throw new BranchException("Branch name is too long");
        if (!NAME_PATTERN.matcher(name).matches()) throw new BranchException("Invalid branch name");
        if (!NAME_LETTER_PATTERN.matcher(name).find()) throw new BranchException("Branch name must contain at least one letter or digit");
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        if (address == null || address.isBlank() || !address.equals(address.strip())) throw new BranchException("Invalid branch address");
        if (address.length() > ADDRESS_MAX_LENGTH) throw new BranchException("Branch address is too long");
        this.address = address;
    }

    public void changeName(String newName) {
        setName(newName);
    }

    public void changeAddress(String newAddress) {
        setAddress(newAddress);
    }

    public void activate() {
        this.status = BranchStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = BranchStatus.INACTIVE;
    }

    public void softDelete() {
        if (deletedAt != null) throw new BranchException("Branch is already deleted");
        this.deletedAt = Instant.now();
        deactivate();
    }

    public void reissueAuthKey(String newHash) {
        if (deletedAt != null) throw new BranchException("Branch is already deleted");
        setAuthKeyHash(newHash);
    }

    public boolean isActive() {
        return status == BranchStatus.ACTIVE;
    }

    public boolean isSoftDeleted() {
        return deletedAt != null;
    }
}
