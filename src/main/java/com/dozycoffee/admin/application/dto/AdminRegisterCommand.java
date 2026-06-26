package com.dozycoffee.admin.application.dto;

public record AdminRegisterCommand(
        String username,
        String password,
        String employeeNo,
        String name,
        String phone,
        String email
) {

}
