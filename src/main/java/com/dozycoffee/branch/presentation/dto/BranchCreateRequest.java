package com.dozycoffee.branch.presentation.dto;

import com.dozycoffee.branch.application.dto.BranchCreateCommand;
import jakarta.validation.constraints.NotBlank;

public record BranchCreateRequest(
        @NotBlank String name,
        @NotBlank String address
) {

    public BranchCreateCommand toCommand() {
        return new BranchCreateCommand(name, address);
    }
}
