package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.dto.BranchProfileUpdateCommand;
import jakarta.validation.constraints.NotBlank;

public record BranchProfileUpdateRequest(
        @NotBlank String name,
        @NotBlank String address
) {

    public BranchProfileUpdateCommand toCommand() {
        return new BranchProfileUpdateCommand(name, address);
    }
}
