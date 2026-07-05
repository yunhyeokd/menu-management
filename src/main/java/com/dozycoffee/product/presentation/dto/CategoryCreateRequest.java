package com.dozycoffee.product.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank String name
) {
}
