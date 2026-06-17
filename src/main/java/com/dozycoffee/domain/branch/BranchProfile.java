package com.dozycoffee.domain.branch;

import com.dozycoffee.domain.auth.BranchStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public class BranchProfile {

    private long branchId;
    private String name;
    private String address;


    private BranchProfile(long branchId, String name, String address) {
        validateRequiredFields(name, address);
        this.branchId = branchId;
        this.name = name;
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BranchProfile branchProfile)) return false;
        return Objects.equals(branchId, branchProfile.branchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchId);
    }

    public static BranchProfile of(long branchId, String name, String address) {
        return new BranchProfile(branchId, name, address);
    }

    public static BranchProfile create(long branchId, String name, String address) {
        validateName(name);
        validateAddress(address);
        return new BranchProfile(branchId, name, address);
    }

    private static void validateRequiredFields(String name, String address) {
        if (name == null) throw new BranchException("name cannot be null");
        if (address == null) throw new BranchException("address cannot be null");
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

    public Long getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
