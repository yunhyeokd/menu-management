package com.dozycoffee.branch.application;

import com.dozycoffee.auth.application.FakeSessionInvalidationPort;
import com.dozycoffee.auth.application.PasswordHasher;
import com.dozycoffee.branch.application.dto.BranchAuthKeyReissueResult;
import com.dozycoffee.branch.application.dto.BranchCreateResult;
import com.dozycoffee.branch.application.dto.BranchProfileUpdateCommand;
import com.dozycoffee.branch.domain.*;
import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.ServiceError;
import com.dozycoffee.core.application.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BranchServiceTest {

    private FakeBranchAccountRepository branchAccountRepository;
    private FakeBranchProfileRepository branchProfileRepository;
    private FakeBranchProductLifecyclePort productLifecyclePort;
    private FakeSessionInvalidationPort sessionInvalidationPort;
    private BranchService branchService;
    private PasswordHasher passwordHasher;

    private long nextBranchId = 1L;
    private long nextCodeSeq = 1L;

    @BeforeEach
    public void setUp() {
        branchAccountRepository = new FakeBranchAccountRepository();
        branchProfileRepository = new FakeBranchProfileRepository();
        productLifecyclePort = new FakeBranchProductLifecyclePort();
        sessionInvalidationPort = new FakeSessionInvalidationPort();
        nextBranchId = 1L;
        nextCodeSeq = 1L;
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

        branchService = new BranchService(
                branchAccountRepository,
                branchProfileRepository,
                productLifecyclePort,
                () -> BranchId.of(nextBranchId++),
                () -> BranchCode.of(String.format("2026%04d", nextCodeSeq++)),
                () -> () -> "raw-auth-key",
                passwordHasher,
                sessionInvalidationPort
        );
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    // ─── reissueAuthKey ───────────────────────────────────────────────────────

    @Test
    public void 인증키를_정상_재발급한다() {
        BranchId branchId = BranchId.of(1L);
        BranchAccount account = BranchFixture.builder().id(branchId).authKeyHash("old-hash").build();
        branchAccountRepository.put(account);

        BranchAuthKeyReissueResult result = branchService.reissueAuthKey(branchId);

        assertThat(result.rawAuthKey()).isEqualTo("raw-auth-key");
        branchAccountRepository.findById(branchId).ifPresent(branchAccount -> {
            assertThat(branchAccount.getAuthKeyHash()).isEqualTo("hashed-raw-auth-key");
            assertThat(sessionInvalidationPort.wasInvalidated(branchAccount)).isTrue();
        });
    }

    @Test
    public void 인증키_재발급시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.reissueAuthKey(BranchId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    public void 인증키_재발급시_소프트_삭제된_지점이면_ALREADY_DELETED_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        BranchAccount account = BranchFixture.builder().id(branchId).build();
        account.softDelete();
        branchAccountRepository.put(account);

        assertThatThrownBy(() -> branchService.reissueAuthKey(branchId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.ALREADY_DELETED_ERROR));
    }

    @Test
    public void 인증키_재발급중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.reissueAuthKey(branchId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── create ──────────────────────────────────────────────────────────────

    @Test
    public void 지점을_정상_생성한다() {
        BranchCreateResult result = branchService.create("강남점", "서울 강남구 테헤란로 123");

        assertThat(result.branchId()).isNotNull();
        assertThat(result.branchCode()).isNotNull();
        assertThat(result.rawAuthKey()).isEqualTo("raw-auth-key");
        assertThat(result.name()).isEqualTo("강남점");
        assertThat(result.address()).isEqualTo("서울 강남구 테헤란로 123");
        assertThat(result.createdAt()).isNotNull();
        assertThat(branchAccountRepository.contains(result.branchId())).isTrue();
        assertThat(branchProfileRepository.contains(result.branchId())).isTrue();
    }

    @Test
    public void 지점_생성시_이름이_중복되면_DUPLICATE_NAME_ERROR를_던진다() {
        branchProfileRepository.put(BranchProfile.create(BranchId.of(99L), "강남점", "서울 강남구 테헤란로 1"));

        assertThatThrownBy(() -> branchService.create("강남점", "서울 강남구 테헤란로 123"))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.DUPLICATE_NAME_ERROR));
    }

    @Test
    public void 지점_생성시_이름이_유효하지_않으면_INVALID_BRANCH_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.create("", "서울 강남구 테헤란로 123"))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.INVALID_BRANCH_ERROR));
    }

    @Test
    public void 지점_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        branchProfileRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.create("강남점", "서울 강남구 테헤란로 123"))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── updateProfile ────────────────────────────────────────────────────────

    @Test
    public void 지점_프로필을_정상_수정한다() {
        BranchId branchId = BranchId.of(1L);
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));

        branchService.updateProfile(branchId, new BranchProfileUpdateCommand("강남역점", "서울 강남구 강남대로 456"));

        Optional<BranchProfile> updated = branchProfileRepository.findById(branchId);
        updated.ifPresent(branchProfile -> {
            assertThat(branchProfile.getName()).isEqualTo("강남역점");
            assertThat(branchProfile.getAddress()).isEqualTo("서울 강남구 강남대로 456");
        });
    }

    @Test
    public void 지점_프로필_수정시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.updateProfile(BranchId.of(999L), new BranchProfileUpdateCommand("강남점", "서울 강남구 테헤란로 123")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    public void 지점_프로필_수정시_이름이_유효하지_않으면_INVALID_PROFILE_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));

        assertThatThrownBy(() -> branchService.updateProfile(branchId, new BranchProfileUpdateCommand("", "서울 강남구 테헤란로 123")))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.INVALID_PROFILE_ERROR));
    }

    @Test
    public void 지점_프로필_수정중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));
        branchProfileRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.updateProfile(branchId, new BranchProfileUpdateCommand("강남역점", "서울 강남구 강남대로 456")))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── softDelete ───────────────────────────────────────────────────────────

    @Test
    public void 지점을_정상_소프트_삭제한다() {
        BranchId branchId = BranchId.of(1L);
        BranchAccount account = BranchFixture.builder().id(branchId).build();
        branchAccountRepository.put(account);

        branchService.softDelete(branchId);

        branchAccountRepository.findById(branchId).ifPresent(a ->
                assertThat(a.getDeletedAt()).isNotNull()
        );
        assertThat(productLifecyclePort.wasDeactivated(branchId)).isTrue();
        assertThat(sessionInvalidationPort.wasInvalidated(account)).isTrue();
    }

    @Test
    public void 지점_소프트_삭제시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.softDelete(BranchId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    public void 이미_소프트_삭제된_지점을_다시_삭제하면_ALREADY_DELETED_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        BranchAccount account = BranchFixture.builder().id(branchId).build();
        account.softDelete();
        branchAccountRepository.put(account);

        assertThatThrownBy(() -> branchService.softDelete(branchId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.ALREADY_DELETED_ERROR));
    }

    @Test
    public void 지점_소프트_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.softDelete(branchId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── hardDelete ───────────────────────────────────────────────────────────

    @Test
    public void 지점을_정상_하드_삭제한다() {
        BranchId branchId = BranchId.of(1L);
        BranchAccount account = BranchFixture.builder().id(branchId).build();
        branchAccountRepository.put(account);
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));

        branchService.softDelete(branchId);
        branchService.hardDelete(branchId);

        assertThat(branchAccountRepository.contains(branchId)).isFalse();
        assertThat(branchProfileRepository.contains(branchId)).isFalse();
        assertThat(productLifecyclePort.wasDeleted(branchId)).isTrue();
        assertThat(sessionInvalidationPort.wasInvalidated(account)).isTrue();
    }

    @Test
    public void 소프트_삭제되지_않은_지점을_하드_삭제하면_INVALID_BRANCH_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));

        assertThatThrownBy(() -> branchService.hardDelete(branchId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.INVALID_BRANCH_ERROR));
    }

    @Test
    public void 지점_하드_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        BranchAccount account = BranchFixture.builder().id(branchId).build();
        account.softDelete();
        branchAccountRepository.put(account);
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));
        productLifecyclePort.throwOnNextCall();

        assertThatThrownBy(() -> branchService.hardDelete(branchId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }
}
