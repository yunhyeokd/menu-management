package com.dozycoffee.domain.branch;

public class BranchFixture {

    public static long id = 1L;
    public static String code = "20260001";
    public static String authKeyHash = "authKeyHash";

    public static BranchBuilder builder() {
        return new BranchBuilder();
    }

    public static class BranchBuilder {

        private String code = BranchFixture.code;
        private String authKeyHash = BranchFixture.authKeyHash;

        public BranchBuilder code(String code) {
            this.code = code;
            return this;
        }

        public BranchBuilder authKeyHash(String authKeyHash) {
            this.authKeyHash = authKeyHash;
            return this;
        }

        public BranchAccount build() {
            return BranchAccount.create(code, authKeyHash);
        }
    }
}
