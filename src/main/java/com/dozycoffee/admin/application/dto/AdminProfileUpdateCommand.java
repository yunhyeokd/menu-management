package com.dozycoffee.admin.application.dto;

public record AdminProfileUpdateCommand(
        String name,
        String phone,
        String email
) {
}
