package com.dozycoffee.product.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
        @NotBlank String name
) {
}
