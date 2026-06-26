package com.dozycoffee.auth.application;

import com.dozycoffee.admin.domain.AdminAccount;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;
import com.dozycoffee.core.application.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthorizationServiceTest {

    private AuthorizationService authorizationService;
    private AdminAccount activeAdmin;

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService();
        activeAdmin = AdminAccount.create(AdminId.of(1L), AdminRole.SYSTEM, "sysadmin", "hashed-password");
    }

    private void assertErrorCode(Throwable e, AuthErrors error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    @Test
    void role이_일치하면_인가에_성공한다() {
        authorizationService.authorize(activeAdmin, List.of("SYSTEM"));
    }

    @Test
    void 여러_role_중_하나라도_일치하면_인가에_성공한다() {
        authorizationService.authorize(activeAdmin, List.of("STAFF", "SYSTEM"));
    }

    @Test
    void role이_일치하지_않으면_UNAUTHORIZED_예외가_발생한다() {
        assertThatThrownBy(() -> authorizationService.authorize(activeAdmin, List.of("BRANCH")))
                .satisfies(e -> assertErrorCode(e, AuthErrors.UNAUTHORIZED));
    }
}
