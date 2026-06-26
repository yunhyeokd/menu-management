package com.dozycoffee.admin.application.dto;

import java.util.Optional;

public record AdminProfileUpdateCommand(
        Optional<String> name,
        Optional<String> phone,
        Optional<String> email
) {
}
