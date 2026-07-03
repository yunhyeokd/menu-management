package com.dozycoffee.admin.domain;

public class AdminFixture {

    public static AdminId id = AdminId.of("00000000-0000-0000-0000-000000000001");

    public static AdminBuilder builder() {
        return new AdminBuilder();
    }

    public static SystemAdminBuilder system() {
        return new SystemAdminBuilder();
    }

    public static AdminProfile defaultProfile() {
        return AdminProfile.create("EMP001", "홍길동", "+821012345678", "admin@dozy.com");
    }

    public static class AdminBuilder {

        private AdminId id = AdminFixture.id;
        private String username = "test";
        private String password = "password";
        private AdminProfile profile = defaultProfile();

        public AdminBuilder id(AdminId id) { this.id = id; return this; }
        public AdminBuilder username(String username) { this.username = username; return this; }
        public AdminBuilder password(String password) { this.password = password; return this; }
        public AdminBuilder profile(AdminProfile profile) { this.profile = profile; return this; }

        public Admin build() {
            return Admin.create(id, username, password, profile);
        }
    }

    public static class SystemAdminBuilder {

        private AdminId id = AdminFixture.id;
        private String username = "sysadmin";
        private String password = "password";

        public SystemAdminBuilder id(AdminId id) { this.id = id; return this; }
        public SystemAdminBuilder username(String username) { this.username = username; return this; }
        public SystemAdminBuilder password(String password) { this.password = password; return this; }

        public SystemAdmin build() {
            return SystemAdmin.create(id, username, password);
        }
    }
}
