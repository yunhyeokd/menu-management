package com.dozycoffee.admin.application;

import com.dozycoffee.core.security.FakePasswordHasher;
import com.dozycoffee.core.security.PasswordHasher;
import com.dozycoffee.admin.domain.*;
import com.dozycoffee.core.security.Credential;
import com.dozycoffee.core.security.Principal;
import com.dozycoffee.core.id.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AdminUsernameAuthenticatorTest {

    private FakeAdminRepository adminRepository;
    private AdminUsernameAuthenticator adminUsernameAuthenticator;

    private SystemAdmin activeAdmin;
    private Admin pendingAdmin;

    private static final String RAW_PASSWORD = "password";
    private static final String HASHED_PASSWORD = "hashed-password";

    @BeforeEach
    void setUp() {
        adminRepository = new FakeAdminRepository();

        PasswordHasher passwordHasher = new FakePasswordHasher();

        adminUsernameAuthenticator = new AdminUsernameAuthenticator(adminRepository, passwordHasher);

        activeAdmin = AdminFixture.system().id(AdminFixture.id).username("sysadmin").password(HASHED_PASSWORD).build();
        pendingAdmin = AdminFixture.builder()
                .id(AdminId.of("00000000-0000-0000-0000-000000000002"))
                .username("pendingadmin")
                .password(HASHED_PASSWORD)
                .build();

        adminRepository.put(activeAdmin);
        adminRepository.put(pendingAdmin);
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
        Optional<Principal> result = adminUsernameAuthenticator.authenticate(username("pendingadmin"), credential(RAW_PASSWORD));
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
