package com.dozycoffee.domain.branch;

public class BranchProfileFixture {

    public static BranchId branchId = BranchId.of(1L);
    public static String name = "어린이대공원";
    public static String address = "서울 광진구 능동로 195-16";

    public static BranchProfileBuilder builder() {
        return new BranchProfileBuilder();
    }

    public static class BranchProfileBuilder {

        private BranchId branchId = BranchProfileFixture.branchId;
        private String name = BranchProfileFixture.name;
        private String address = BranchProfileFixture.address;

        public BranchProfileBuilder branchId(BranchId branchId) {
            this.branchId = branchId;
            return this;
        }

        public BranchProfileBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BranchProfileBuilder address(String address) {
            this.address = address;
            return this;
        }

        public BranchProfile build() {
            return BranchProfile.create(branchId, name, address);
        }
    }
}
