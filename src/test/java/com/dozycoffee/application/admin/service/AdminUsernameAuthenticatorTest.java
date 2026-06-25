package com.dozycoffee.application.admin.service;

import com.dozycoffee.application.admin.repository.FakeAdminAccountRepository;
import com.dozycoffee.application.auth.PasswordHasher;
import com.dozycoffee.domain.admin.AdminAccount;
import com.dozycoffee.domain.admin.AdminId;
import com.dozycoffee.domain.admin.AdminRole;
import com.dozycoffee.domain.auth.Credential;
import com.dozycoffee.domain.auth.Principal;
import com.dozycoffee.domain.common.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AdminUsernameAuthenticatorTest {

    private FakeAdminAccountRepository adminAccountRepository;
    private AdminUsernameAuthenticator adminUsernameAuthenticator;

    private AdminAccount activeAdmin;
    private AdminAccount pendingAdmin;

    private static final String RAW_PASSWORD = "password";
    private static final String HASHED_PASSWORD = "hashed-password";

    @BeforeEach
    void setUp() {
        adminAccountRepository = new FakeAdminAccountRepository();

        PasswordHasher passwordHasher = new PasswordHasher() {
            @Override
            public String hash(String raw) { return "hashed-" + raw; }

            @Override
            public boolean matches(String raw, String hash) { return hash(raw).equals(hash); }
        };

        adminUsernameAuthenticator = new AdminUsernameAuthenticator(adminAccountRepository, passwordHasher);

        activeAdmin = AdminAccount.create(AdminId.of(1L), AdminRole.SYSTEM, "sysadmin", HASHED_PASSWORD);
        pendingAdmin = AdminAccount.create(AdminId.of(2L), AdminRole.STAFF, "staffadmin", HASHED_PASSWORD);

        adminAccountRepository.put(activeAdmin);
        adminAccountRepository.put(pendingAdmin);
    }

    private Identifier<String> username(String value) {
        return () -> value;
    }

    private Credential credential(String value) {
        return () -> value;
    }

    @Test
    void ACTIVE_계정은_자격증명이_일치하면_인증에_성공한다() {
        Optional<Principal> result = adminUsernameAuthenticator.authenticate(username("sysadmin"), credential(RAW_PASSWORD));

        assertThat(result).contains(activeAdmin);
    }

    @Test
    void PENDING_계정은_자격증명이_일치해도_인증에_실패한다() {
        Optional<Principal> result = adminUsernameAuthenticator.authenticate(username("staffadmin"), credential(RAW_PASSWORD));

        assertThat(result).isEmpty();
    }

    @Test
    void 비밀번호가_틀리면_인증에_실패한다() {
        Optional<Principal> result = adminUsernameAuthenticator.authenticate(username("sysadmin"), credential("wrong"));

        assertThat(result).isEmpty();
    }

    @Test
    void 존재하지_않는_username이면_인증에_실패한다() {
        Optional<Principal> result = adminUsernameAuthenticator.authenticate(username("unknown"), credential(RAW_PASSWORD));

        assertThat(result).isEmpty();
    }
}
