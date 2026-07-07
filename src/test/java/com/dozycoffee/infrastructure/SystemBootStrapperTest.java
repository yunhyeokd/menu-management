package com.dozycoffee.infrastructure;

import com.dozycoffee.admin.application.AdminService;
import com.dozycoffee.admin.application.FakeAdminRepository;
import com.dozycoffee.admin.domain.AdminId;
import com.dozycoffee.auth.application.FakeSessionInvalidationPort;
import com.dozycoffee.core.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SystemBootStrapperTest {

    private FakeAdminRepository adminRepository;
    private AdminService adminService;
    private long nextAdminId = 1L;

    @BeforeEach
    void setUp() {
        adminRepository = new FakeAdminRepository();
        PasswordHasher passwordHasher = new PasswordHasher() {
            @Override
            public String hash(String raw) {
                return "hashed-" + raw;
            }

            @Override
            public boolean matches(String raw, String hash) {
                return hash(raw).equals(hash);
            }
        };
        adminService = new AdminService(
                adminRepository,
                () -> AdminId.of(String.format("00000000-0000-0000-0000-%012d", nextAdminId++)),
                passwordHasher,
                new FakeSessionInvalidationPort()
        );
    }

    @Test
    void 환경변수가_비어있으면_아무것도_생성하지_않는다() {
        new SystemBootStrapper(adminService, "", "").bootstrap();

        assertThat(adminRepository.findSystemAdmin()).isEmpty();
    }

    @Test
    void 환경변수가_설정되어있으면_시스템_관리자를_생성한다() {
        new SystemBootStrapper(adminService, "sysadmin", "password1234").bootstrap();

        assertThat(adminRepository.findSystemAdmin()).isPresent();
        assertThat(adminRepository.findSystemAdmin().get().getUsername()).isEqualTo("sysadmin");
    }

    @Test
    void 이미_시스템_관리자가_있으면_예외없이_스킵한다() {
        new SystemBootStrapper(adminService, "sysadmin", "password1234").bootstrap();

        assertThatCode(() -> new SystemBootStrapper(adminService, "another", "password5678").bootstrap())
                .doesNotThrowAnyException();

        assertThat(adminRepository.findAll()).hasSize(1);
        assertThat(adminRepository.findSystemAdmin().get().getUsername()).isEqualTo("sysadmin");
    }
}
