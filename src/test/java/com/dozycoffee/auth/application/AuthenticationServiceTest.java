package com.dozycoffee.auth.application;

import com.dozycoffee.admin.domain.Admin;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.admin.domain.AdminRole;
import com.dozycoffee.auth.domain.Principal;
import com.dozycoffee.branch.domain.Branch;

import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.auth.domain.Credential;
import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthenticationServiceTest {

    private static final String ADMIN_ROLE = "SYSTEM";
    private static final String BRANCH_ROLE = "BRANCH";
    private static final String UNKNOWN_ROLE = "UNKNOWN";

    private static final String RAW_PASSWORD = "password";
    private static final String HASHED_PASSWORD = "hashed-password";
    private static final String BRANCH_CODE = "20240001";
    private static final String RAW_AUTH_KEY = "auth-key";
    private static final String HASHED_AUTH_KEY = "hashed-auth-key";

    private AuthenticationService authService;
    private Admin activeAdmin;
    private Branch activeBranch;

    @BeforeEach
    void setUp() {
        activeAdmin = Admin.create(AdminId.of("00000000-0000-0000-0000-000000000001"), AdminRole.SYSTEM, "sysadmin", HASHED_PASSWORD, null);
        activeBranch = Branch.create(BranchId.of("00000000-0000-0000-0000-000000000001"), BranchCode.of(BRANCH_CODE), HASHED_AUTH_KEY, "테스트점", "서울 강남구 테헤란로 1");

        AuthenticationResolver adminResolver = (id, credential) -> {
            if ("sysadmin".equals(id) && HASHED_PASSWORD.equals("hashed-" + credential.getValue())) {
                return new AuthenticationResult(activeAdmin, 3600L);
            }
            throw new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.INVALID_CREDENTIAL);
        };

        AuthenticationResolver branchResolver = (id, credential) -> {
            if (BRANCH_CODE.equals(id) && HASHED_AUTH_KEY.equals("hashed-" + credential.getValue())) {
                return new AuthenticationResult(activeBranch, 86400L);
            }
            throw new AuthenticationException(AuthServiceCode.AUTH, AuthErrors.INVALID_CREDENTIAL);
        };

        authService = new AuthenticationService(
                Map.of(ADMIN_ROLE, adminResolver, BRANCH_ROLE, branchResolver)
        );
    }

    private void assertErrorCode(Throwable e, AuthErrors error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    @Test
    void 관리자_인증_성공시_Principal과_TTL을_반환한다() {
        AuthenticationResult result = authService.authenticate(ADMIN_ROLE, "sysadmin", () -> RAW_PASSWORD);

        assertThat(result.principal()).isEqualTo(activeAdmin);
        assertThat(result.sessionTtl()).isEqualTo(3600L);
    }

    @Test
    void 관리자_인증_실패시_INVALID_CREDENTIAL_예외가_발생한다() {
        assertThatThrownBy(() -> authService.authenticate(ADMIN_ROLE, "sysadmin", () -> "wrong"))
                .satisfies(e -> assertErrorCode(e, AuthErrors.INVALID_CREDENTIAL));
    }

    @Test
    void 지점_인증_성공시_Principal과_TTL을_반환한다() {
        AuthenticationResult result = authService.authenticate(BRANCH_ROLE, BRANCH_CODE, () -> RAW_AUTH_KEY);

        assertThat(result.principal()).isEqualTo(activeBranch);
        assertThat(result.sessionTtl()).isEqualTo(86400L);
    }

    @Test
    void 지점_인증_실패시_INVALID_CREDENTIAL_예외가_발생한다() {
        assertThatThrownBy(() -> authService.authenticate(BRANCH_ROLE, BRANCH_CODE, () -> "wrong"))
                .satisfies(e -> assertErrorCode(e, AuthErrors.INVALID_CREDENTIAL));
    }

    @Test
    void 등록되지_않은_role로_인증_시_UNSUPPORTED_ROLE_예외가_발생한다() {
        assertThatThrownBy(() -> authService.authenticate(UNKNOWN_ROLE, "id", () -> "credential"))
                .satisfies(e -> assertErrorCode(e, AuthErrors.UNSUPPORTED_ROLE));
    }
}
