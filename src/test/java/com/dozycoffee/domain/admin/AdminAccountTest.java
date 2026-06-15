package com.dozycoffee.domain.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AdminAccountTest {

    @Test
    public void 관리자_계정을_정상_생성한다() {
        String username = "test";
        String password = "password";

        AdminAccount adminAccount = AdminAccount.create(AdminRole.STAFF, username, password);

        assertThat(adminAccount.getId()).isNull();
        assertThat(adminAccount.getUsername()).isEqualTo(username);
        assertThat(adminAccount.getRole()).isEqualTo(AdminRole.STAFF);
        assertThat(adminAccount.getStatus()).isEqualTo(AdminStatus.PENDING);
    }

    @Test
    public void 관리자_계정_생성시_역할이_null이면_안된다() {
        assertThatThrownBy(() -> AdminFixture.builder().role(null).build()).isInstanceOf(AdminException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaaa", "aaaaaaaaaaaaaaaaaaaa"})
    public void 관리자_계정_생성시_경계_길이의_로그인아이디는_정상_생성된다(String validUsername) {
        AdminAccount account = AdminFixture.builder().username(validUsername).build();
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
    public void 같은_id를_가진_계정은_동등하다() {
        AdminAccount a = AdminAccount.of(1L, AdminRole.STAFF, "staff_user", "password", AdminStatus.ACTIVE, Instant.now(), null);
        AdminAccount b = AdminAccount.of(1L, AdminRole.STAFF, "staff_user", "password", AdminStatus.ACTIVE, Instant.now(), null);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void 다른_id를_가진_계정은_동등하지_않다() {
        AdminAccount a = AdminAccount.of(1L, AdminRole.STAFF, "staff_user", "password", AdminStatus.ACTIVE, Instant.now(), null);
        AdminAccount b = AdminAccount.of(2L, AdminRole.STAFF, "staff_user", "password", AdminStatus.ACTIVE, Instant.now(), null);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    public void id가_null인_계정은_동등하지_않다() {
        AdminAccount a = AdminFixture.builder().build();
        AdminAccount b = AdminFixture.builder().build();

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    public void 시스템_관리자_계정은_ACTIVE_상태로_생성된다() {
        AdminAccount system = AdminFixture.system().build();
        assertThat(system.getStatus()).isEqualTo(AdminStatus.ACTIVE);
    }

    @Test
    public void 스태프_관리자_계정은_PENDING_상태로_생성된다() {
        AdminAccount staff = AdminFixture.builder().role(AdminRole.STAFF).build();
        assertThat(staff.getStatus()).isEqualTo(AdminStatus.PENDING);
    }

    @ParameterizedTest
    @NullSource
    @EnumSource(value = AdminStatus.class, names = {"ACTIVE"}, mode = EnumSource.Mode.EXCLUDE)
    public void 시스템_관리자_계정의_상태는_변경할_수_없다(AdminStatus status) {
        AdminAccount system = AdminFixture.system().build();
        assertThatThrownBy(() -> system.updateStatus(status)).isInstanceOf(AdminException.class);
    }

    @ParameterizedTest
    @NullSource
    @EnumSource(value = AdminStatus.class)
    public void 스태프_관리자_계정의_상태는_변경할_수_있다(AdminStatus status) {
        AdminAccount staff = AdminFixture.builder().role(AdminRole.STAFF).build();
        staff.updateStatus(status);
        assertThat(staff.getStatus()).isEqualTo(status);
    }

}
