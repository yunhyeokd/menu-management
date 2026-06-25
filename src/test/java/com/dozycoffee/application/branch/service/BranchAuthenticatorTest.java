package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.auth.PasswordHasher;
import com.dozycoffee.application.branch.repository.FakeBranchAccountRepository;
import com.dozycoffee.domain.auth.Credential;
import com.dozycoffee.domain.auth.Principal;
import com.dozycoffee.domain.branch.BranchAccount;
import com.dozycoffee.domain.branch.BranchCode;
import com.dozycoffee.domain.branch.BranchId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BranchAuthenticatorTest {

    private FakeBranchAccountRepository branchAccountRepository;
    private BranchAuthenticator branchAuthenticator;

    private BranchAccount activeBranch;
    private BranchAccount deletedBranch;

    private static final String RAW_AUTH_KEY = "auth-key";
    private static final String HASHED_AUTH_KEY = "hashed-auth-key";
    private static final BranchCode ACTIVE_CODE = BranchCode.of("20240001");
    private static final BranchCode DELETED_CODE = BranchCode.of("20240002");

    @BeforeEach
    void setUp() {
        branchAccountRepository = new FakeBranchAccountRepository();

        PasswordHasher passwordHasher = new PasswordHasher() {
            @Override
            public String hash(String raw) { return "hashed-" + raw; }

            @Override
            public boolean matches(String raw, String hash) { return hash(raw).equals(hash); }
        };

        branchAuthenticator = new BranchAuthenticator(branchAccountRepository, passwordHasher);

        activeBranch = BranchAccount.create(BranchId.of(1L), ACTIVE_CODE, HASHED_AUTH_KEY);
        deletedBranch = BranchAccount.create(BranchId.of(2L), DELETED_CODE, HASHED_AUTH_KEY);
        deletedBranch.softDelete();

        branchAccountRepository.put(activeBranch);
        branchAccountRepository.put(deletedBranch);
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
