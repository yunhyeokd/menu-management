package com.dozycoffee.domain.branch;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class Branch {

    Long id;
    String code;
    String name;
    String address;
    String authKeyHash;
    BranchStatus status;
    Instant createdAt;
    Instant deletedAt;


    private Branch(Long id, String code, String name, String address, String authKeyHash, BranchStatus status, Instant createdAt, Instant deletedAt) {
        validateRequiredFields(code, name, address, authKeyHash, status, createdAt);
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.authKeyHash = authKeyHash;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Branch branch)) return false;
        if (id == null || branch.id == null) return false;
        return id.equals(branch.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Branch of(long id, String code, String name, String address, String authKeyHash, BranchStatus status, Instant createdAt, Instant deletedAt) {
        return new Branch(id, code, name, address, authKeyHash, status, createdAt, deletedAt);
    }

    public static Branch create(String code, String name, String address, String authKeyHash) {
        validateCode(code);
        validateName(name);
        validateAddress(address);
        validateAuthKeyHash(authKeyHash);
        return new Branch(null, code, name, address, authKeyHash, BranchStatus.ACTIVE, Instant.now(), null);
    }

    private static void validateRequiredFields(String code, String name, String address, String authKeyHash, BranchStatus status, Instant createdAt) {
        if (code == null) throw new BranchException("code cannot be null");
        if (name == null) throw new BranchException("name cannot be null");
        if (address == null) throw new BranchException("address cannot be null");
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

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ \\-()&.]+$");

    private static final Pattern NAME_LETTER_PATTERN =
            Pattern.compile("[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]");

    private static final int NAME_MAX_LENGTH = 30;

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BranchException("Invalid branch name");
        }
        if (!name.equals(name.strip())) {
            throw new BranchException("Branch name must not have leading or trailing whitespace");
        }
        if (name.length() > NAME_MAX_LENGTH) {
            throw new BranchException("Branch name is too long");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new BranchException("Invalid branch name");
        }
        if (!NAME_LETTER_PATTERN.matcher(name).find()) {
            throw new BranchException("Branch name must contain at least one letter or digit");
        }
    }

    private static final int MAX_ADDRESS_LENGTH = 255;

    private static void validateAddress(String address) {
        if (address == null || address.isBlank() || !address.equals(address.strip())) {
            throw new BranchException("Invalid branch address");
        }
        if (address.length() > MAX_ADDRESS_LENGTH) {
            throw new BranchException("Branch address is too long");
        }
    }

    private static void validateAuthKeyHash(String authKeyHash) {
        if (authKeyHash == null || authKeyHash.isBlank()) {
            throw new BranchException("Invalid branch address");
        }
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
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
