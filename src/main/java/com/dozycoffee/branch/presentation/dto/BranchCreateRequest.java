package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.dto.BranchCreateCommand;

public record BranchCreateRequest(
        String name,
        String address
) {

    public BranchCreateCommand toCommand() {
        return new BranchCreateCommand(name, address);
    }
}
