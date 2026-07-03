package com.dozycoffee.admin.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemAdminTest {

    @Test
    public void 시스템_관리자_계정은_ACTIVE_상태로_생성된다() {
        SystemAdmin system = AdminFixture.system().build();
        assertThat(system.getStatus()).isEqualTo(AdminStatus.ACTIVE);
    }

    @Test
    public void 시스템_관리자_계정의_role은_SYSTEM이다() {
        SystemAdmin system = AdminFixture.system().build();
        assertThat(system.getRole()).isEqualTo("SYSTEM");
    }

    @Test
    public void 비밀번호를_정상_변경한다() {
        SystemAdmin system = AdminFixture.system().build();
        system.updatePasswordHash("newPassword");
        assertThat(system.getPasswordHash()).isEqualTo("newPassword");
    }
}
