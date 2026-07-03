package com.dozycoffee.admin.domain;

public class AdminFixture {

    public static AdminId id = AdminId.of("00000000-0000-0000-0000-000000000001");

    public static AdminBuilder builder() {
        return new AdminBuilder();
    }

    public static AdminBuilder system() {
        return new AdminBuilder().role(AdminRole.SYSTEM).profile(null);
    }

    public static AdminProfile defaultProfile() {
        return AdminProfile.create("EMP001", "홍길동", "+821012345678", "admin@dozy.com");
    }

    public static class AdminBuilder {

        private AdminId id = AdminFixture.id;
        private AdminRole role = AdminRole.ADMIN;
        private String username = "test";
        private String password = "password";
        private AdminProfile profile = defaultProfile();

        public AdminBuilder id(AdminId id) { this.id = id; return this; }
        public AdminBuilder role(AdminRole role) { this.role = role; return this; }
        public AdminBuilder username(String username) { this.username = username; return this; }
        public AdminBuilder password(String password) { this.password = password; return this; }
        public AdminBuilder profile(AdminProfile profile) { this.profile = profile; return this; }

        public Admin build() {
            return Admin.create(id, role, username, password, profile);
        }
    }
}
