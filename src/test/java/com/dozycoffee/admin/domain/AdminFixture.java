package com.dozycoffee.domain.admin;

import com.dozycoffee.admin.domain.AdminAccount;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;

public class AdminFixture {

    public static AdminId id = AdminId.of(1L);

    public static AdminAccountBuilder builder() {
        return new AdminAccountBuilder();
    }

    public static AdminAccountBuilder system() {
        return new AdminAccountBuilder().role(AdminRole.SYSTEM);
    }

    public static class AdminAccountBuilder {

        private AdminId id = AdminFixture.id;
        private AdminRole role = AdminRole.STAFF;
        private String username = "test";
        private String password = "password";

        public AdminAccountBuilder id(AdminId id) { this.id = id; return this; }
        public AdminAccountBuilder role(AdminRole role) { this.role = role; return this; }
        public AdminAccountBuilder username(String username) { this.username = username; return this; }
        public AdminAccountBuilder password(String password) { this.password = password; return this; }

        public AdminAccount build() {
            return AdminAccount.create(id, role, username, password);
        }
    }
}
