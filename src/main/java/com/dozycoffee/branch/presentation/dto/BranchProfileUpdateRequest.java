package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.dto.BranchProfileUpdateCommand;

public record BranchProfileUpdateRequest(
        String name,
        String address
) {

    public BranchProfileUpdateCommand toCommand() {
        return new BranchProfileUpdateCommand(name, address);
    }
}
