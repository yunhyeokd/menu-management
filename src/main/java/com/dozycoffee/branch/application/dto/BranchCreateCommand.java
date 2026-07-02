package com.dozycoffee.branch.application.dto;

public record BranchCreateCommand(
        String name,
        String address
) {
}
