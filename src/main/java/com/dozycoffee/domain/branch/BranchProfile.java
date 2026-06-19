package com.dozycoffee.domain.branch;

import java.util.Objects;
import java.util.regex.Pattern;

public class BranchProfile {

    private BranchId branchId;
    private String name;
    private String address;


    private BranchProfile(BranchId branchId, String name, String address) {
        setBranchId(branchId);
        setName(name);
        setAddress(address);
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

    public static BranchProfile of(BranchId branchId, String name, String address) {
        return new BranchProfile(branchId, name, address);
    }

    public static BranchProfile create(BranchId branchId, String name, String address) {
        return new BranchProfile(branchId, name, address);
    }

    public BranchId getBranchId() {
        return branchId;
    }

    private void setBranchId(BranchId branchId) {
        if (branchId == null) throw new BranchException("branchId cannot be null");
        this.branchId = branchId;
    }

    public String getName() {
        return name;
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

    private void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public String getAddress() {
        return address;
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

    private void setAddress(String address) {
        validateAddress(address);
        this.address = address;
    }

    public void changeName(String newName) {
        setName(newName);
    }

    public void changeAddress(String newAddress) {
        setAddress(newAddress);
    }
}
