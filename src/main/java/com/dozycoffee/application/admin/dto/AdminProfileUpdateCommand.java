package com.dozycoffee.application.admin.dto;

import java.util.Optional;

public record AdminProfileUpdateCommand(
        Optional<String> name,
        Optional<String> phone,
        Optional<String> email
) {
}
