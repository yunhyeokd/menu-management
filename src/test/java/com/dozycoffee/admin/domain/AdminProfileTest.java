package com.dozycoffee.admin.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AdminProfileTest {

    static class Mock {
        static String employeeNo = "20260001";
        static String name = "John Doe";
        static String phone = "+821012345678";
        static String email = "abc@example.com";
    }

    @Test
    public void 관리자_프로필을_정상_생성한다() {
        AdminProfile profile = AdminProfile.create(Mock.employeeNo, Mock.name, Mock.phone, Mock.email);
        assertThat(profile.getEmployeeNo()).isEqualTo(Mock.employeeNo);
        assertThat(profile.getName()).isEqualTo(Mock.name);
        assertThat(profile.getPhone()).isEqualTo(Mock.phone);
        assertThat(profile.getEmail()).isEqualTo(Mock.email);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "aaaa", "AB@#",
            "aaaaaaaaaaaaaaaaaaaab"
    })
    public void 프로필_생성시_사원번호가_유효하지_않으면_예외가_발생한다(String employeeNo) {
        assertThatThrownBy(() -> AdminProfile.create(employeeNo, Mock.name, Mock.phone, Mock.email))
                .isInstanceOf(AdminException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ", "\n",
            " John Doe", "John Doe ",
            "ㄱ현우",
            "1234",
            "abc@!@#",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 프로필_생성시_이름이_유효하지_않으면_예외가_발생한다(String name) {
        assertThatThrownBy(() -> AdminProfile.create(Mock.employeeNo, name, Mock.phone, Mock.email))
                .isInstanceOf(AdminException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+1234567",
            "+821012345678",
            "+82212345678",
            "+12345678901234"
    })
    public void 프로필_생성시_유효한_전화번호는_정상_생성된다(String phone) {
        AdminProfile profile = AdminProfile.create(Mock.employeeNo, Mock.name, phone, Mock.email);
        assertThat(profile.getPhone()).isEqualTo(phone);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ", "\n",
            "821012345678",
            "+0821012345678",
            "+12345",
            "+1234567890123456",
            "+82-1012-345678"
    })
    public void 프로필_생성시_전화번호가_유효하지_않으면_예외가_발생한다(String phone) {
        assertThatThrownBy(() -> AdminProfile.create(Mock.employeeNo, Mock.name, phone, Mock.email))
                .isInstanceOf(AdminException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user@example.com",
            "user.name+tag@example.co.kr",
            "user@sub.domain.com"
    })
    public void 프로필_생성시_유효한_이메일은_정상_생성된다(String email) {
        AdminProfile profile = AdminProfile.create(Mock.employeeNo, Mock.name, Mock.phone, email);
        assertThat(profile.getEmail()).isEqualTo(email);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", "  ",
            "userexample.com",
            "user@",
            "@example.com",
            "user@example"
    })
    public void 프로필_생성시_이메일이_유효하지_않으면_예외가_발생한다(String email) {
        assertThatThrownBy(() -> AdminProfile.create(Mock.employeeNo, Mock.name, Mock.phone, email))
                .isInstanceOf(AdminException.class);
    }

    @Test
    public void 프로필_생성시_이메일이_255자를_초과하면_예외가_발생한다() {
        String local = "a".repeat(64);
        String domain = "b".repeat(200);
        String email = local + "@" + domain + ".com";
        assertThatThrownBy(() -> AdminProfile.create(Mock.employeeNo, Mock.name, Mock.phone, email))
                .isInstanceOf(AdminException.class);
    }

    @Test
    public void 같은_employeeNo를_가진_프로필은_동등하다() {
        AdminProfile a = AdminProfile.of(Mock.employeeNo, Mock.name, Mock.phone, Mock.email);
        AdminProfile b = AdminProfile.of(Mock.employeeNo, Mock.name, Mock.phone, Mock.email);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void 다른_employeeNo를_가진_프로필은_동등하지_않다() {
        AdminProfile a = AdminProfile.of("EMP001", Mock.name, Mock.phone, Mock.email);
        AdminProfile b = AdminProfile.of("EMP002", Mock.name, Mock.phone, Mock.email);

        assertThat(a).isNotEqualTo(b);
    }
}
