package com.dozycoffee.domain.admin;

public class AdminFixture {

    public static AdminAccountBuilder builder() {
        return new AdminAccountBuilder();
    }

    public static AdminAccountBuilder system() {
        return new AdminAccountBuilder().role(AdminRole.SYSTEM);
    }

    public static class AdminAccountBuilder {

        private AdminRole role = AdminRole.STAFF;
        private String username   = "test";
        private String password   = "password";

        public AdminAccountBuilder role(AdminRole role) { this.role = role; return this; }
        public AdminAccountBuilder username(String username)     { this.username = username; return this; }
        public AdminAccountBuilder password(String password)     { this.password = password; return this; }

        public AdminAccount build() {
            return AdminAccount.create(role, username, password);
        }
    }
}