package com.dozycoffee.branch.domain;

public class BranchFixture {

    public static BranchId id = BranchId.of("00000000-0000-0000-0000-000000000001");
    public static BranchCode code = BranchCode.of("20260001");
    public static String authKeyHash = "authKeyHash";
    public static String name = "어린이대공원";
    public static String address = "서울 광진구 능동로 195-16";

    public static BranchBuilder builder() {
        return new BranchBuilder();
    }

    public static class BranchBuilder {

        private BranchId id = BranchFixture.id;
        private String code = "20260001";
        private String authKeyHash = BranchFixture.authKeyHash;
        private String name = BranchFixture.name;
        private String address = BranchFixture.address;

        public BranchBuilder id(BranchId id) {
            this.id = id;
            return this;
        }

        public BranchBuilder code(String code) {
            this.code = code;
            return this;
        }

        public BranchBuilder authKeyHash(String authKeyHash) {
            this.authKeyHash = authKeyHash;
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

        public Branch build() {
            return Branch.create(id, BranchCode.of(code), authKeyHash, name, address);
        }
    }
}
