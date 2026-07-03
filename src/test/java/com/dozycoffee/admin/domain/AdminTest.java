package com.dozycoffee.admin.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AdminTest {

    private static AdminProfile defaultProfile() {
        return AdminProfile.create("EMP001", "홍길동", "+821012345678", "admin@dozy.com");
    }

    @Test
    public void 관리자_계정을_정상_생성한다() {
        Admin adminAccount = Admin.create(AdminId.of("00000000-0000-0000-0000-000000000001"), AdminRole.ADMIN, "test", "password", defaultProfile());

        assertThat(adminAccount.getUsername()).isEqualTo("test");
        assertThat(adminAccount.getRole()).isEqualTo("ADMIN");
        assertThat(adminAccount.getStatus()).isEqualTo(AdminStatus.PENDING);
    }

    @Test
    public void 관리자_계정_생성시_역할이_null이면_안된다() {
        assertThatThrownBy(() -> AdminFixture.builder().role(null).build()).isInstanceOf(AdminException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaaa", "aaaaaaaaaaaaaaaaaaaa"})
    public void 관리자_계정_생성시_경계_길이의_로그인아이디는_정상_생성된다(String validUsername) {
        Admin account = AdminFixture.builder().username(validUsername).build();
        assertThat(account.getUsername()).isEqualTo(validUsername);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "1234", "abcd@", "ab", "aaa", "aaaaaaaaaaaaaaaaaaaaa"})
    public void 관리자_계정_생성시_로그인아이디가_유효하지_않으면_예외가_발생한다(String invalidUsername) {
        assertThatThrownBy(() -> AdminFixture.builder().username(invalidUsername).build())
                .isInstanceOf(AdminException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {""})
    public void 관리자_계정_생성시_비밀번호가_유효하지_않으면_예외가_발생한다(String invalidPassword) {
        assertThatThrownBy(() -> AdminFixture.builder().password(invalidPassword).build())
                .isInstanceOf(AdminException.class);
    }

    @Test
    public void ADMIN_계정_생성시_profile이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Admin.create(AdminId.of("00000000-0000-0000-0000-000000000001"), AdminRole.ADMIN, "test", "password", null))
                .isInstanceOf(AdminException.class);
    }

    @Test
    public void SYSTEM_계정은_profile이_null이어도_생성된다() {
        Admin system = Admin.create(AdminId.of("00000000-0000-0000-0000-000000000001"), AdminRole.SYSTEM, "sysadmin", "password", null);
        assertThat(system.getProfile()).isNull();
    }

    @Test
    public void 같은_id를_가진_계정은_동등하다() {
        AdminProfile profile = defaultProfile();
        Admin a = Admin.of(AdminId.of("00000000-0000-0000-0000-000000000001"), AdminRole.ADMIN, "admin_user", "password", AdminStatus.ACTIVE, Instant.now(), null, profile);
        Admin b = Admin.of(AdminId.of("00000000-0000-0000-0000-000000000001"), AdminRole.ADMIN, "admin_user", "password", AdminStatus.ACTIVE, Instant.now(), null, profile);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void 다른_id를_가진_계정은_동등하지_않다() {
        AdminProfile profile = defaultProfile();
        Admin a = Admin.of(AdminId.of("00000000-0000-0000-0000-000000000001"), AdminRole.ADMIN, "admin_user", "password", AdminStatus.ACTIVE, Instant.now(), null, profile);
        Admin b = Admin.of(AdminId.of("00000000-0000-0000-0000-000000000002"), AdminRole.ADMIN, "admin_user", "password", AdminStatus.ACTIVE, Instant.now(), null, profile);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    public void 시스템_관리자_계정은_ACTIVE_상태로_생성된다() {
        Admin system = AdminFixture.system().build();
        assertThat(system.getStatus()).isEqualTo(AdminStatus.ACTIVE);
    }

    @Test
    public void 일반_관리자_계정은_PENDING_상태로_생성된다() {
        Admin admin = AdminFixture.builder().role(AdminRole.ADMIN).build();
        assertThat(admin.getStatus()).isEqualTo(AdminStatus.PENDING);
    }

    @ParameterizedTest
    @NullSource
    @EnumSource(value = AdminStatus.class, names = {"ACTIVE"}, mode = EnumSource.Mode.EXCLUDE)
    public void 시스템_관리자_계정의_상태는_변경할_수_없다(AdminStatus status) {
        Admin system = AdminFixture.system().build();
        assertThatThrownBy(() -> system.updateStatus(status)).isInstanceOf(AdminException.class);
    }

    @ParameterizedTest
    @EnumSource(value = AdminStatus.class)
    public void 일반_관리자_계정의_상태는_변경할_수_있다(AdminStatus status) {
        Admin admin = AdminFixture.builder().role(AdminRole.ADMIN).build();
        admin.updateStatus(status);
        assertThat(admin.getStatus()).isEqualTo(status);
    }

    @Test
    public void 관리자_계정_상태_변경시_null이면_예외가_발생한다() {
        Admin admin = AdminFixture.builder().role(AdminRole.ADMIN).build();
        assertThatThrownBy(() -> admin.updateStatus(null)).isInstanceOf(AdminException.class);
    }

    @Test
    public void 관리자_계정을_소프트_삭제하면_deletedAt이_설정된다() {
        Admin admin = AdminFixture.builder().role(AdminRole.ADMIN).build();
        assertThat(admin.getDeletedAt()).isNull();
        admin.softDelete();
        assertThat(admin.getDeletedAt()).isNotNull();
    }

    @Test
    public void 시스템_관리자_계정은_소프트_삭제할_수_없다() {
        Admin system = AdminFixture.system().build();
        assertThatThrownBy(system::softDelete).isInstanceOf(AdminException.class);
    }

    @Test
    public void 이미_삭제된_계정을_다시_삭제하면_예외가_발생한다() {
        Admin admin = AdminFixture.builder().role(AdminRole.ADMIN).build();
        admin.softDelete();
        assertThatThrownBy(admin::softDelete).isInstanceOf(AdminException.class);
    }

    @Test
    public void 비밀번호를_정상_변경한다() {
        Admin admin = AdminFixture.builder().role(AdminRole.ADMIN).build();
        admin.updatePasswordHash("newPassword");
        assertThat(admin.getPasswordHash()).isEqualTo("newPassword");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {""})
    public void 비밀번호_변경시_유효하지_않으면_예외가_발생한다(String invalidPassword) {
        Admin admin = AdminFixture.builder().role(AdminRole.ADMIN).build();
        assertThatThrownBy(() -> admin.updatePasswordHash(invalidPassword)).isInstanceOf(AdminException.class);
    }
}
