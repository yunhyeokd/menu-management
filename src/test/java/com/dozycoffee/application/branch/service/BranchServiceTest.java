package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.branch.dto.BranchCreateResult;
import com.dozycoffee.application.branch.dto.BranchProfileUpdateDto;
import com.dozycoffee.application.branch.repository.FakeBranchAccountRepository;
import com.dozycoffee.application.branch.repository.FakeBranchProfileRepository;
import com.dozycoffee.application.branch.repository.FakeProductSalesOverrideRepository;
import com.dozycoffee.application.product.repository.FakeProductRepository;
import com.dozycoffee.domain.branch.*;
import com.dozycoffee.domain.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BranchServiceTest {

    private FakeBranchAccountRepository branchAccountRepository;
    private FakeBranchProfileRepository branchProfileRepository;
    private FakeProductRepository productRepository;
    private FakeProductSalesOverrideRepository productSalesOverrideRepository;
    private BranchService branchService;

    private long nextBranchId = 1L;
    private long nextCodeSeq = 1L;
    private long nextOverrideId = 1L;

    @BeforeEach
    public void setUp() {
        branchAccountRepository = new FakeBranchAccountRepository();
        branchProfileRepository = new FakeBranchProfileRepository();
        productRepository = new FakeProductRepository();
        productSalesOverrideRepository = new FakeProductSalesOverrideRepository();
        nextBranchId = 1L;
        nextCodeSeq = 1L;
        nextOverrideId = 1L;

        branchService = new BranchService(
                branchAccountRepository,
                branchProfileRepository,
                productRepository,
                productSalesOverrideRepository,
                () -> BranchId.of(nextBranchId++),
                () -> BranchCode.of(String.format("2026%04d", nextCodeSeq++)),
                () -> "raw-auth-key",
                raw -> "hashed-" + raw
        );
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
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.DUPLICATE_NAME_ERROR.errorCode));
    }

    @Test
    public void 지점_생성시_이름이_유효하지_않으면_INVALID_BRANCH_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.create("", "서울 강남구 테헤란로 123"))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.INVALID_BRANCH_ERROR.errorCode));
    }

    @Test
    public void 지점_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        branchProfileRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.create("강남점", "서울 강남구 테헤란로 123"))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── updateProfile ────────────────────────────────────────────────────────

    @Test
    public void 지점_프로필을_정상_수정한다() {
        BranchId branchId = BranchId.of(1L);
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));

        branchService.updateProfile(branchId, new BranchProfileUpdateDto("강남역점", "서울 강남구 강남대로 456"));

        BranchProfile updated = branchProfileRepository.findById(branchId);
        assertThat(updated.getName()).isEqualTo("강남역점");
        assertThat(updated.getAddress()).isEqualTo("서울 강남구 강남대로 456");
    }

    @Test
    public void 지점_프로필_수정시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.updateProfile(BranchId.of(999L), new BranchProfileUpdateDto("강남점", "서울 강남구 테헤란로 123")))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.BRANCH_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 지점_프로필_수정시_이름이_유효하지_않으면_INVALID_PROFILE_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));

        assertThatThrownBy(() -> branchService.updateProfile(branchId, new BranchProfileUpdateDto("", "서울 강남구 테헤란로 123")))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.INVALID_PROFILE_ERROR.errorCode));
    }

    @Test
    public void 지점_프로필_수정중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));
        branchProfileRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.updateProfile(branchId, new BranchProfileUpdateDto("강남역점", "서울 강남구 강남대로 456")))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── softDelete ───────────────────────────────────────────────────────────

    @Test
    public void 지점을_정상_소프트_삭제한다() {
        BranchId branchId = BranchId.of(1L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productRepository.put(ProductFixture.builder().id(ProductId.of(10L)).branchId(branchId).status(ProductStatus.ACTIVE).build());

        branchService.softDelete(branchId);

        BranchAccount account = branchAccountRepository.findById(branchId);
        assertThat(account.getDeletedAt()).isNotNull();
        assertThat(productRepository.all())
                .allMatch(p -> p.getStatus() == ProductStatus.INACTIVE);
    }

    @Test
    public void 지점_소프트_삭제시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.softDelete(BranchId.of(999L)))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.BRANCH_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 이미_소프트_삭제된_지점을_다시_삭제하면_ALREADY_DELETED_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        BranchAccount account = BranchFixture.builder().id(branchId).build();
        account.softDelete();
        branchAccountRepository.put(account);

        assertThatThrownBy(() -> branchService.softDelete(branchId))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.ALREADY_DELETED_ERROR.errorCode));
    }

    @Test
    public void 지점_소프트_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.softDelete(branchId))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── hardDelete ───────────────────────────────────────────────────────────

    @Test
    public void 지점을_정상_하드_삭제한다() {
        BranchId branchId = BranchId.of(1L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        branchProfileRepository.put(BranchProfile.create(branchId, "강남점", "서울 강남구 테헤란로 123"));
        productRepository.put(ProductFixture.builder().id(ProductId.of(10L)).branchId(branchId).build());

        branchService.hardDelete(branchId);

        assertThat(branchAccountRepository.contains(branchId)).isFalse();
        assertThat(branchProfileRepository.contains(branchId)).isFalse();
        assertThat(productRepository.all()).isEmpty();
    }

    @Test
    public void 지점_하드_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        productRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.hardDelete(BranchId.of(1L)))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── findOverridableProducts ──────────────────────────────────────────────

    @Test
    public void 재정의_가능한_상품_목록을_조회한다() {
        BranchId branchId = BranchId.of(1L);
        BranchId otherBranchId = BranchId.of(2L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        Product commonProduct = ProductFixture.builder().id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).status(ProductStatus.ACTIVE).build();
        Product activeBranchExclusive = ProductFixture.builder().id(ProductId.of(2L)).branchId(branchId).status(ProductStatus.ACTIVE).build();
        Product inactiveBranchExclusive = ProductFixture.builder().id(ProductId.of(3L)).branchId(branchId).status(ProductStatus.INACTIVE).build();
        Product otherBranchProduct = ProductFixture.builder().id(ProductId.of(4L)).branchId(otherBranchId).status(ProductStatus.ACTIVE).build();
        productRepository.put(commonProduct);
        productRepository.put(activeBranchExclusive);
        productRepository.put(inactiveBranchExclusive);
        productRepository.put(otherBranchProduct);

        List<Product> result = branchService.findOverridableProducts(branchId);

        assertThat(result).containsExactlyInAnyOrder(commonProduct, activeBranchExclusive);
    }

    @Test
    public void 재정의_가능한_상품_조회시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.findOverridableProducts(BranchId.of(999L)))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.BRANCH_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 재정의_가능한_상품_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.findOverridableProducts(branchId))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── hideSale ─────────────────────────────────────────────────────────────

    @Test
    public void 상품_판매를_정상_숨긴다() {
        BranchId branchId = BranchId.of(1L);
        ProductId productId = ProductId.of(10L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productRepository.put(ProductFixture.builder().id(productId).status(ProductStatus.ACTIVE).build());

        branchService.hideSale(branchId, productId);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId);
        assertThat(override).isNotNull();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.HIDDEN);
    }

    @Test
    public void 이미_품절_처리된_상품을_숨기면_HIDDEN으로_상태가_변경된다() {
        BranchId branchId = BranchId.of(1L);
        ProductId productId = ProductId.of(10L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productRepository.put(ProductFixture.builder().id(productId).status(ProductStatus.ACTIVE).build());
        productSalesOverrideRepository.put(ProductSalesOverride.of(1L, productId, branchId, ProductSalesOverrideStatus.SOLD_OUT, Instant.now()));

        branchService.hideSale(branchId, productId);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId);
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.HIDDEN);
    }

    @Test
    public void 판매_숨기기시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.hideSale(BranchId.of(999L), ProductId.of(10L)))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.BRANCH_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 판매_숨기기시_상품이_존재하지_않으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());

        assertThatThrownBy(() -> branchService.hideSale(branchId, ProductId.of(999L)))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.PRODUCT_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 판매_숨기기시_상품이_비활성이면_PRODUCT_NOT_ACTIVE_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        ProductId productId = ProductId.of(10L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productRepository.put(ProductFixture.builder().id(productId).status(ProductStatus.INACTIVE).build());

        assertThatThrownBy(() -> branchService.hideSale(branchId, productId))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.PRODUCT_NOT_ACTIVE_ERROR.errorCode));
    }

    @Test
    public void 판매_숨기기중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        ProductId productId = ProductId.of(10L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productRepository.put(ProductFixture.builder().id(productId).status(ProductStatus.ACTIVE).build());
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.hideSale(branchId, productId))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── soldOut ──────────────────────────────────────────────────────────────

    @Test
    public void 상품을_정상_품절_처리한다() {
        BranchId branchId = BranchId.of(1L);
        ProductId productId = ProductId.of(10L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productRepository.put(ProductFixture.builder().id(productId).status(ProductStatus.ACTIVE).build());

        branchService.soldOut(branchId, productId);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId);
        assertThat(override).isNotNull();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.SOLD_OUT);
    }

    @Test
    public void 이미_숨겨진_상품을_품절_처리하면_SOLD_OUT으로_상태가_변경된다() {
        BranchId branchId = BranchId.of(1L);
        ProductId productId = ProductId.of(10L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productRepository.put(ProductFixture.builder().id(productId).status(ProductStatus.ACTIVE).build());
        productSalesOverrideRepository.put(ProductSalesOverride.of(1L, productId, branchId, ProductSalesOverrideStatus.HIDDEN, Instant.now()));

        branchService.soldOut(branchId, productId);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId);
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.SOLD_OUT);
    }

    @Test
    public void 품절_처리시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.soldOut(BranchId.of(999L), ProductId.of(10L)))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.BRANCH_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 품절_처리시_상품이_존재하지_않으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());

        assertThatThrownBy(() -> branchService.soldOut(branchId, ProductId.of(999L)))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.PRODUCT_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 품절_처리시_상품이_비활성이면_PRODUCT_NOT_ACTIVE_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        ProductId productId = ProductId.of(10L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productRepository.put(ProductFixture.builder().id(productId).status(ProductStatus.INACTIVE).build());

        assertThatThrownBy(() -> branchService.soldOut(branchId, productId))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.PRODUCT_NOT_ACTIVE_ERROR.errorCode));
    }

    @Test
    public void 품절_처리중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of(1L);
        ProductId productId = ProductId.of(10L);
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productRepository.put(ProductFixture.builder().id(productId).status(ProductStatus.ACTIVE).build());
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.soldOut(branchId, productId))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.UNKNOWN_ERROR.errorCode));
    }

    // ─── restoreSale ──────────────────────────────────────────────────────────

    @Test
    public void 판매_재개를_정상_처리한다() {
        BranchId branchId = BranchId.of(1L);
        ProductId productId = ProductId.of(10L);
        productSalesOverrideRepository.put(ProductSalesOverride.of(1L, productId, branchId, ProductSalesOverrideStatus.HIDDEN, Instant.now()));

        branchService.restoreSale(branchId, productId);

        assertThat(productSalesOverrideRepository.contains(1L)).isFalse();
    }

    @Test
    public void 판매_재개시_판매_재정의가_없으면_SALES_OVERRIDE_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> branchService.restoreSale(BranchId.of(1L), ProductId.of(10L)))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.SALES_OVERRIDE_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 판매_재개중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        productSalesOverrideRepository.throwOnNextCall();

        assertThatThrownBy(() -> branchService.restoreSale(BranchId.of(1L), ProductId.of(10L)))
                .isInstanceOf(BranchBusinessException.class)
                .satisfies(e -> assertThat(((BranchBusinessException) e).getErrorCode())
                        .isEqualTo(BranchErrors.UNKNOWN_ERROR.errorCode));
    }
}
