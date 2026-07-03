package com.dozycoffee.admin.application;

import com.dozycoffee.admin.application.dto.*;
import com.dozycoffee.admin.domain.*;
import com.dozycoffee.auth.application.FakeSessionInvalidationPort;
import com.dozycoffee.auth.application.PasswordHasher;
import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AdminServiceTest {

    private FakeAdminRepository adminRepository;
    private FakeSessionInvalidationPort sessionInvalidationPort;
    private PasswordHasher passwordHasher;

    private AdminService adminService;

    private long nextAdminId = 1L;

    @BeforeEach
    public void setUp() {
        adminRepository = new FakeAdminRepository();
        sessionInvalidationPort = new FakeSessionInvalidationPort();
        passwordHasher = new PasswordHasher() {
            @Override
            public String hash(String raw) {
                return "hashed-" + raw;
            }

            @Override
            public boolean matches(String raw, String hash) {
                return hash(raw).equals(hash);
            }
        };
        nextAdminId = 1L;

        adminService = new AdminService(
                adminRepository,
                () -> AdminId.of(String.format("00000000-0000-0000-0000-%012d", nextAdminId++)),
                passwordHasher,
                sessionInvalidationPort
        );
    }

    private Admin adminWithProfile(AdminId id) {
        AdminProfile profile = AdminProfile.create("EMP001", "홍길동", "+821012345678", "admin@dozy.com");
        return Admin.create(id, "adminuser", "password", profile);
    }

    private void assertErrorCode(Throwable e, AdminErrors error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    // ─── registerSystem ───────────────────────────────────────────────────────

    @Test
    public void 시스템_관리자_계정을_정상_생성한다() {
        SystemAdmin result = adminService.registerSystem(
                new SystemAdminRegisterCommand("sysadmin", "password"));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getAdminRole()).isEqualTo(AdminRole.SYSTEM);
        assertThat(result.getUsername()).isEqualTo("sysadmin");
        assertThat(result.getCreatedAt()).isNotNull();

        SystemAdmin saved = adminRepository.findSystemAdmin().orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(AdminStatus.ACTIVE);
        assertThat(saved.getPasswordHash()).isEqualTo("hashed-password");
    }

    @Test
    public void 시스템_관리자가_이미_존재하면_DUPLICATE_ACCOUNT_ERROR를_던진다() {
        adminRepository.put(AdminFixture.system().username("sysadmin").build());

        assertThatThrownBy(() -> adminService.registerSystem(
                new SystemAdminRegisterCommand("another", "password")))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.DUPLICATE_ACCOUNT_ERROR));
    }

    @Test
    public void 시스템_관리자_생성시_username이_유효하지_않으면_INVALID_ADMIN_ERROR를_던진다() {
        assertThatThrownBy(() -> adminService.registerSystem(
                new SystemAdminRegisterCommand("a", "password")))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.INVALID_ADMIN_ERROR));
    }

    @Test
    public void 시스템_관리자_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        adminRepository.throwOnNextCall();

        assertThatThrownBy(() -> adminService.registerSystem(
                new SystemAdminRegisterCommand("sysadmin", "password")))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.UNKNOWN_ERROR));
    }

    // ─── registerAdmin ────────────────────────────────────────────────────────

    @Test
    public void 사원_관리자_계정을_정상_생성한다() {
        Admin result = adminService.registerAdmin(new AdminRegisterCommand(
                "admin01", "password", "EMP001", "홍길동", "+821012345678", "admin@dozy.com"));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getAdminRole()).isEqualTo(AdminRole.ADMIN);
        assertThat(result.getUsername()).isEqualTo("admin01");
        assertThat(result.getProfile().getName()).isEqualTo("홍길동");
        assertThat(result.getProfile().getEmail()).isEqualTo("admin@dozy.com");

        Admin account = adminRepository.findAdminById(result.getId()).orElseThrow();
        assertThat(account.getStatus()).isEqualTo(AdminStatus.PENDING);
        assertThat(account.getProfile()).isNotNull();
    }

    @Test
    public void 사원_관리자_생성시_username이_중복되면_DUPLICATE_ACCOUNT_ERROR를_던진다() {
        adminRepository.put(AdminFixture.builder().username("admin01").build());

        assertThatThrownBy(() -> adminService.registerAdmin(new AdminRegisterCommand(
                "admin01", "password", "EMP002", "김철수", "+821087654321", "kim@dozy.com")))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.DUPLICATE_ACCOUNT_ERROR));
    }

    @Test
    public void 사원_관리자_생성시_사원번호가_중복되면_DUPLICATE_EMPLOYEE_NO_ERROR를_던진다() {
        adminRepository.put(AdminFixture.builder().username("other01").build());

        assertThatThrownBy(() -> adminService.registerAdmin(new AdminRegisterCommand(
                "admin01", "password", "EMP001", "김철수", "+821087654321", "kim@dozy.com")))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.DUPLICATE_EMPLOYEE_NO_ERROR));
    }

    @Test
    public void 사원_관리자_생성시_프로필_값이_유효하지_않으면_INVALID_ADMIN_ERROR를_던진다() {
        assertThatThrownBy(() -> adminService.registerAdmin(new AdminRegisterCommand(
                "admin01", "password", "EMP001", "홍길동", "invalid-phone", "admin@dozy.com")))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.INVALID_ADMIN_ERROR));
    }

    @Test
    public void 사원_관리자_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        adminRepository.throwOnNextCall();

        assertThatThrownBy(() -> adminService.registerAdmin(new AdminRegisterCommand(
                "admin01", "password", "EMP001", "홍길동", "+821012345678", "admin@dozy.com")))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.UNKNOWN_ERROR));
    }

    // ─── approve ──────────────────────────────────────────────────────────────

    @Test
    public void 계정_생성_요청을_정상_승인한다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(AdminFixture.builder().id(adminId).build());

        adminService.approve(adminId);

        Admin account = adminRepository.findAdminById(adminId).orElseThrow();
        assertThat(account.getStatus()).isEqualTo(AdminStatus.ACTIVE);
    }

    @Test
    public void 승인시_계정이_존재하지_않으면_ADMIN_NOT_FOUND를_던진다() {
        assertThatThrownBy(() -> adminService.approve(AdminId.of("00000000-0000-0000-0000-000000000999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.ADMIN_NOT_FOUND));
    }

    @Test
    public void PENDING_상태가_아닌_계정_승인시_UNABLE_APPROVAL_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        Admin account = AdminFixture.builder().id(adminId).build();
        account.approve();
        adminRepository.put(account);

        assertThatThrownBy(() -> adminService.approve(adminId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.UNABLE_APPROVAL_ERROR));
    }

    @Test
    public void 승인중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(AdminFixture.builder().id(adminId).build());
        adminRepository.throwOnNextCall();

        assertThatThrownBy(() -> adminService.approve(adminId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.UNKNOWN_ERROR));
    }

    // ─── reject ───────────────────────────────────────────────────────────────

    @Test
    public void 계정_생성_요청을_정상_거절한다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(AdminFixture.builder().id(adminId).build());

        adminService.reject(adminId);

        Admin account = adminRepository.findAdminById(adminId).orElseThrow();
        assertThat(account.getStatus()).isEqualTo(AdminStatus.INACTIVE);
        assertThat(account.isSoftDeleted()).isTrue();
    }

    @Test
    public void 거절시_계정이_존재하지_않으면_ADMIN_NOT_FOUND를_던진다() {
        assertThatThrownBy(() -> adminService.reject(AdminId.of("00000000-0000-0000-0000-000000000999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.ADMIN_NOT_FOUND));
    }

    @Test
    public void PENDING_상태가_아닌_계정_거절시_UNABLE_APPROVAL_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        Admin account = AdminFixture.builder().id(adminId).build();
        account.approve();
        adminRepository.put(account);

        assertThatThrownBy(() -> adminService.reject(adminId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.UNABLE_APPROVAL_ERROR));
    }

    // ─── updateProfile ────────────────────────────────────────────────────────

    @Test
    public void 관리자_프로필을_정상_수정한다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(adminWithProfile(adminId));

        Admin result = adminService.updateProfile(adminId,
                new AdminProfileUpdateCommand("김철수", "+821099998888", "new@dozy.com"));

        assertThat(result.getProfile().getName()).isEqualTo("김철수");
        assertThat(result.getProfile().getPhone()).isEqualTo("+821099998888");
        assertThat(result.getProfile().getEmail()).isEqualTo("new@dozy.com");
    }

    @Test
    public void 프로필_수정시_계정이_존재하지_않으면_ADMIN_NOT_FOUND를_던진다() {
        assertThatThrownBy(() -> adminService.updateProfile(AdminId.of("00000000-0000-0000-0000-000000000999"),
                new AdminProfileUpdateCommand("김철수", "+821099998888", "new@dozy.com")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.ADMIN_NOT_FOUND));
    }

    @Test
    public void 프로필_수정시_값이_유효하지_않으면_INVALID_ADMIN_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(adminWithProfile(adminId));

        assertThatThrownBy(() -> adminService.updateProfile(adminId,
                new AdminProfileUpdateCommand(null, "invalid-phone", "bad-email")))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.INVALID_ADMIN_ERROR));
    }

    @Test
    public void 프로필_수정중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(adminWithProfile(adminId));
        adminRepository.throwOnNextCall();

        assertThatThrownBy(() -> adminService.updateProfile(adminId,
                new AdminProfileUpdateCommand("김철수", "+821099998888", "new@dozy.com")))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.UNKNOWN_ERROR));
    }

    // ─── softDelete ───────────────────────────────────────────────────────────

    @Test
    public void 관리자_계정을_정상_소프트_삭제하고_세션을_무효화한다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        Admin account = adminRepository.put(AdminFixture.builder().id(adminId).build());

        adminService.softDelete(adminId);

        Admin deleted = adminRepository.findAdminById(adminId).orElseThrow();
        assertThat(deleted.getStatus()).isEqualTo(AdminStatus.INACTIVE);
        assertThat(deleted.isSoftDeleted()).isTrue();
        assertThat(sessionInvalidationPort.wasInvalidated(account)).isTrue();
    }

    @Test
    public void 소프트_삭제시_계정이_존재하지_않으면_ADMIN_NOT_FOUND를_던진다() {
        assertThatThrownBy(() -> adminService.softDelete(AdminId.of("00000000-0000-0000-0000-000000000999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.ADMIN_NOT_FOUND));
    }

    @Test
    public void 시스템_계정의_id로_소프트_삭제_시도시_ADMIN_NOT_FOUND를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(AdminFixture.system().id(adminId).build());

        assertThatThrownBy(() -> adminService.softDelete(adminId))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.ADMIN_NOT_FOUND));
    }

    @Test
    public void 이미_소프트_삭제된_계정을_다시_삭제하면_INVALID_ADMIN_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        Admin account = AdminFixture.builder().id(adminId).build();
        account.softDelete();
        adminRepository.put(account);

        assertThatThrownBy(() -> adminService.softDelete(adminId))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.INVALID_ADMIN_ERROR));
    }

    @Test
    public void 소프트_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(AdminFixture.builder().id(adminId).build());
        adminRepository.throwOnNextCall();

        assertThatThrownBy(() -> adminService.softDelete(adminId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.UNKNOWN_ERROR));
    }

    // ─── hardDelete ───────────────────────────────────────────────────────────

    @Test
    public void 소프트_삭제된_계정을_정상_하드_삭제한다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        Admin account = AdminFixture.builder().id(adminId).build();
        account.softDelete();
        adminRepository.put(account);

        adminService.hardDelete(adminId);

        assertThat(adminRepository.contains(adminId)).isFalse();
        assertThat(sessionInvalidationPort.wasInvalidated(account)).isTrue();
    }

    @Test
    public void 하드_삭제시_계정이_존재하지_않으면_ADMIN_NOT_FOUND를_던진다() {
        assertThatThrownBy(() -> adminService.hardDelete(AdminId.of("00000000-0000-0000-0000-000000000999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.ADMIN_NOT_FOUND));
    }

    @Test
    public void 소프트_삭제되지_않은_계정을_하드_삭제하면_INVALID_ADMIN_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(AdminFixture.builder().id(adminId).build());

        assertThatThrownBy(() -> adminService.hardDelete(adminId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.INVALID_ADMIN_ERROR));
    }

    @Test
    public void 하드_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        Admin account = AdminFixture.builder().id(adminId).build();
        account.softDelete();
        adminRepository.put(account);
        adminRepository.throwOnNextCall();

        assertThatThrownBy(() -> adminService.hardDelete(adminId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.UNKNOWN_ERROR));
    }

    // ─── changePassword ───────────────────────────────────────────────────────

    @Test
    void 비밀번호를_정상_변경한다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(AdminFixture.builder().id(adminId).password("hashed-currentPassword").build());

        adminService.changePassword(adminId, "currentPassword", "newPassword");

        Admin updated = adminRepository.findAdminById(adminId).orElseThrow();
        assertThat(updated.getPasswordHash()).isEqualTo("hashed-newPassword");
    }

    @Test
    void 비밀번호_변경시_계정이_없으면_ADMIN_NOT_FOUND를_던진다() {
        assertThatThrownBy(() -> adminService.changePassword(AdminId.of("00000000-0000-0000-0000-000000000999"), "any", "new"))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.ADMIN_NOT_FOUND));
    }

    @Test
    void 현재_비밀번호가_틀리면_AUTHENTICATION_FAILED_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(AdminFixture.builder().id(adminId).password("hashed-correctPassword").build());

        assertThatThrownBy(() -> adminService.changePassword(adminId, "wrongPassword", "newPassword"))
                .isInstanceOf(AuthenticationException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.AUTHENTICATION_FAILED_ERROR));
    }

    @Test
    void 비밀번호_변경중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        AdminId adminId = AdminId.of("00000000-0000-0000-0000-000000000001");
        adminRepository.put(AdminFixture.builder().id(adminId).password("hashed-currentPassword").build());
        adminRepository.throwOnNextCall();

        assertThatThrownBy(() -> adminService.changePassword(adminId, "currentPassword", "newPassword"))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, AdminErrors.UNKNOWN_ERROR));
    }
}
