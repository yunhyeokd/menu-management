package com.dozycoffee.branch.application;

import com.dozycoffee.core.security.PasswordHasher;
import com.dozycoffee.core.security.Credential;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.branch.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BranchAuthenticatorTest {

    private FakeBranchRepository branchRepository;
    private BranchAuthenticator branchAuthenticator;

    private Branch activeBranch;
    private Branch deletedBranch;

    private static final String RAW_AUTH_KEY = "auth-key";
    private static final String HASHED_AUTH_KEY = "hashed-auth-key";
    private static final BranchCode ACTIVE_CODE = BranchCode.of("20240001");
    private static final BranchCode DELETED_CODE = BranchCode.of("20240002");

    @BeforeEach
    void setUp() {
        branchRepository = new FakeBranchRepository();

        PasswordHasher passwordHasher = new PasswordHasher() {
            @Override
            public String hash(String raw) { return "hashed-" + raw; }

            @Override
            public boolean matches(String raw, String hash) { return hash(raw).equals(hash); }
        };

        branchAuthenticator = new BranchAuthenticator(branchRepository, passwordHasher);

        activeBranch = Branch.create(BranchId.of("00000000-0000-0000-0000-000000000001"), ACTIVE_CODE, HASHED_AUTH_KEY, BranchFixture.name, BranchFixture.address);
        deletedBranch = Branch.create(BranchId.of("00000000-0000-0000-0000-000000000002"), DELETED_CODE, HASHED_AUTH_KEY, BranchFixture.name, BranchFixture.address);
        deletedBranch.softDelete();

        branchRepository.put(activeBranch);
        branchRepository.put(deletedBranch);
    }

    private Credential credential(String value) {
        return () -> value;
    }

    @Test
    void 활성_지점은_자격증명이_일치하면_인증에_성공한다() {
        Optional<Principal> result = branchAuthenticator.authenticate(ACTIVE_CODE, credential(RAW_AUTH_KEY));
        assertThat(result).contains(activeBranch);
    }

    @Test
    void 소프트_삭제된_지점은_자격증명이_일치해도_인증에_실패한다() {
        Optional<Principal> result = branchAuthenticator.authenticate(DELETED_CODE, credential(RAW_AUTH_KEY));
        assertThat(result).isEmpty();
    }

    @Test
    void 인증키가_틀리면_인증에_실패한다() {
        Optional<Principal> result = branchAuthenticator.authenticate(ACTIVE_CODE, credential("wrong"));
        assertThat(result).isEmpty();
    }

    @Test
    void 존재하지_않는_지점코드면_인증에_실패한다() {
        Optional<Principal> result = branchAuthenticator.authenticate(BranchCode.of("99999999"), credential(RAW_AUTH_KEY));
        assertThat(result).isEmpty();
    }
}
