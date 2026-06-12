package com.dozycoffee.domain.branch;

public class BranchFixture {

    public static long id = 1L;
    public static String code = "20260001";
    public static String name = "어린이대공원";
    public static String address = "서울 광진구 능동로 195-16";
    public static String authKeyHash = "authKeyHash";

    public static BranchBuilder builder() {
        return new BranchBuilder();
    }

    public static class BranchBuilder {

        private String code = BranchFixture.code;
        private String name = BranchFixture.name;
        private String address = BranchFixture.address;
        private String authKeyHash = BranchFixture.authKeyHash;

        public BranchBuilder code(String code) {
            this.code = code;
            return this;
        }

        public BranchBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BranchBuilder address(String address) {
            this.address = address;
            return this;
        }

        public BranchBuilder authKeyHash(String authKeyHash) {
            this.authKeyHash = authKeyHash;
            return this;
        }

        public Branch build() {
            return Branch.create(code, name, address, authKeyHash);
        }

    }

}
