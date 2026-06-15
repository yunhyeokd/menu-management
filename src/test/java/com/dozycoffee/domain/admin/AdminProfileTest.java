package com.dozycoffee.domain.admin;

import com.dozycoffee.domain.common.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AdminProfileTest {

    static class Mock {
        static long adminId = 1L;
        static String employeeNo = "20260001";
        static String name = "John Doe";
        static String phone = "+821012345678";
        static String email = "abc@example.com";
    }

    @Test
    public void 관리자_프로필을_정상_생성한다() {

        AdminProfile profile = AdminProfile.create(Mock.adminId, Mock.employeeNo, Mock.name, Mock.phone, Mock.email);
        assertThat(profile.getEmployeeNo()).isEqualTo(Mock.employeeNo);
        assertThat(profile.getName()).isEqualTo(Mock.name);
        assertThat(profile.getPhone()).isEqualTo(Mock.phone);
        assertThat(profile.getEmail()).isEqualTo(Mock.email);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            // 빈 문자열
            "", " ",
            // 대문자/숫자 외 문자
            "aaaa", "AB@#",
            // 20자 초과
            "aaaaaaaaaaaaaaaaaaaab"
    })
    public void 프로필_생성시_사원번호가_유효하지_않으면_예외가_발생한다(String employeeNo) {
        assertThatThrownBy(() -> AdminProfile.create(Mock.adminId, employeeNo, Mock.name, Mock.phone, Mock.email))
                .isInstanceOf(DomainException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            // 빈 문자열
            "", " ", "\n",
            // 앞뒤 공백 문자
            " John Doe", "John Doe ",
            // 유효하지 않은 한글
            "ㄱ현우",
            // 숫자
            "1234",
            // 특수문자
            "abc@!@#",
            // 51자
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    public void 프로필_생성시_이름이_유효하지_않으면_예외가_발생한다(String name) {
        assertThatThrownBy(() -> AdminProfile.create(Mock.adminId, Mock.employeeNo, name, Mock.phone, Mock.email))
                .isInstanceOf(DomainException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+1234567",           // 7자 최솟값
            "+821012345678",     // 한국 휴대폰
            "+82212345678",      // 한국 유선
            "+12345678901234"    // 15자 최댓값
    })
    public void 프로필_생성시_유효한_전화번호는_정상_생성된다(String phone) {
        AdminProfile profile = AdminProfile.create(Mock.adminId, Mock.employeeNo, Mock.name, phone, Mock.email);
        assertThat(profile.getPhone()).isEqualTo(phone);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ", "\n",         // 빈 문자열
            "821012345678",        // + 없음
            "+0821012345678",      // 0으로 시작하는 국가 코드
            "+12345",              // 6자, 최솟값 미만
            "+1234567890123456",   // 16자, 최댓값 초과
            "+82-1012-345678"      // 하이픈 포함
    })
    public void 프로필_생성시_전화번호가_유효하지_않으면_예외가_발생한다(String phone) {
        assertThatThrownBy(() -> AdminProfile.create(Mock.adminId, Mock.employeeNo, Mock.name, phone, Mock.email))
                .isInstanceOf(DomainException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user@example.com",
            "user.name+tag@example.co.kr",
            "user@sub.domain.com"
    })
    public void 프로필_생성시_유효한_이메일은_정상_생성된다(String email) {
        AdminProfile profile = AdminProfile.create(Mock.adminId, Mock.employeeNo, Mock.name, Mock.phone, email);
        assertThat(profile.getEmail()).isEqualTo(email);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", "  ",           // 빈 문자열
            "userexample.com",  // @ 없음
            "user@",            // 도메인 없음
            "@example.com",     // local 없음
            "user@example"      // TLD 없음
    })
    public void 프로필_생성시_이메일이_유효하지_않으면_예외가_발생한다(String email) {
        assertThatThrownBy(() -> AdminProfile.create(Mock.adminId, Mock.employeeNo, Mock.name, Mock.phone, email))
                .isInstanceOf(DomainException.class);
    }

    @Test
    public void 프로필_생성시_이메일이_255자를_초과하면_예외가_발생한다() {
        String local = "a".repeat(64);
        String domain = "b".repeat(200);
        String email = local + "@" + domain + ".com"; // 256자
        assertThatThrownBy(() -> AdminProfile.create(Mock.adminId, Mock.employeeNo, Mock.name, Mock.phone, email))
                .isInstanceOf(DomainException.class);
    }

    @Test
    public void 같은_adminId를_가진_프로필은_동등하다() {
        AdminProfile a = AdminProfile.of(1L, Mock.employeeNo, Mock.name, Mock.phone, Mock.email);
        AdminProfile b = AdminProfile.of(1L, Mock.employeeNo, Mock.name, Mock.phone, Mock.email);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void 다른_adminId를_가진_프로필은_동등하지_않다() {
        AdminProfile a = AdminProfile.of(1L, Mock.employeeNo, Mock.name, Mock.phone, Mock.email);
        AdminProfile b = AdminProfile.of(2L, Mock.employeeNo, Mock.name, Mock.phone, Mock.email);

        assertThat(a).isNotEqualTo(b);
    }

}
