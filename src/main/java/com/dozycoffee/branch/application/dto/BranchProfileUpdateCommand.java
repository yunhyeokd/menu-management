package com.dozycoffee.branch.application.dto;

public record BranchProfileUpdateCommand(
        String name,
        String address
) {
}
