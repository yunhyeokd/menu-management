package com.dozycoffee.domain.branch;

public class BranchFixture {

    public static BranchId id = BranchId.of(1L);
    public static BranchCode code = BranchCode.of("20260001");
    public static String authKeyHash = "authKeyHash";

    public static BranchBuilder builder() {
        return new BranchBuilder();
    }

    public static class BranchBuilder {

        private BranchId id = BranchFixture.id;
        private String code = "20260001";
        private String authKeyHash = BranchFixture.authKeyHash;

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

        public BranchAccount build() {
            return BranchAccount.create(id, BranchCode.of(code), authKeyHash);
        }
    }
}
