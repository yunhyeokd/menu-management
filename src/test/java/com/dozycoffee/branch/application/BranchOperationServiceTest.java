package com.dozycoffee.branch.application;

import com.dozycoffee.branch.application.model.BranchProduct;
import com.dozycoffee.branch.domain.*;
import com.dozycoffee.core.exception.base.*;
import com.dozycoffee.core.exception.service.*;
import com.dozycoffee.product.domain.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BranchOperationServiceTest {

    private static final BranchId NON_EXISTENT_BRANCH_ID = BranchId.of("00000000-0000-0000-0000-000000000999");
    private static final ProductId NON_EXISTENT_PRODUCT_ID = ProductId.of("00000000-0000-0000-0000-000000000999");
    private static final ProductId PRODUCT_ID = ProductId.of("00000000-0000-0000-0000-000000000010");

    private FakeBranchRepository branchAccountRepository;
    private FakeBranchProductQueryPort productQueryPort;
    private FakeProductSalesOverrideRepository productSalesOverrideRepository;
    private BranchOperationService operationService;

    @BeforeEach
    void setUp() {
        branchAccountRepository = new FakeBranchRepository();
        productQueryPort = new FakeBranchProductQueryPort();
        productSalesOverrideRepository = new FakeProductSalesOverrideRepository();
        operationService = new BranchOperationService(
                branchAccountRepository,
                productQueryPort,
                productSalesOverrideRepository
        );
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((ServiceException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    // ─── findOverridableProducts ──────────────────────────────────────────────

    @Test
    void 재정의_가능한_상품_목록을_조회한다() {
        BranchId branchId = BranchFixture.id;
        BranchId otherBranchId = BranchId.of("00000000-0000-0000-0000-000000000002");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());

        BranchProduct commonProduct = BranchProduct.of(ProductId.of("00000000-0000-0000-0000-000000000001"), null, true);
        BranchProduct activeBranchExclusive = BranchProduct.of(ProductId.of("00000000-0000-0000-0000-000000000002"), branchId, true);
        BranchProduct inactiveBranchExclusive = BranchProduct.of(ProductId.of("00000000-0000-0000-0000-000000000003"), branchId, false);
        BranchProduct otherBranchProduct = BranchProduct.of(ProductId.of("00000000-0000-0000-0000-000000000004"), otherBranchId, true);
        productQueryPort.put(commonProduct);
        productQueryPort.put(activeBranchExclusive);
        productQueryPort.put(inactiveBranchExclusive);
        productQueryPort.put(otherBranchProduct);

        List<BranchProduct> result = operationService.findOverridableProducts(branchId);

        assertThat(result).containsExactlyInAnyOrder(commonProduct, activeBranchExclusive);
    }

    @Test
    void 재정의_가능한_상품_조회시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> operationService.findOverridableProducts(NON_EXISTENT_BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    void 재정의_가능한_상품_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> operationService.findOverridableProducts(branchId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── hideSale ─────────────────────────────────────────────────────────────

    @Test
    void 상품_판매를_정상_숨긴다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(PRODUCT_ID, branchId, true));

        operationService.hideSale(branchId, PRODUCT_ID);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, PRODUCT_ID).orElseThrow();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.HIDDEN);
    }

    @Test
    void 이미_품절_처리된_상품을_숨기면_HIDDEN으로_상태가_변경된다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(PRODUCT_ID, branchId, true));
        productSalesOverrideRepository.put(ProductSalesOverride.of(PRODUCT_ID, branchId, ProductSalesOverrideStatus.SOLD_OUT, Instant.now()));

        operationService.hideSale(branchId, PRODUCT_ID);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, PRODUCT_ID).orElseThrow();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.HIDDEN);
    }

    @Test
    void 판매_숨기기시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> operationService.hideSale(NON_EXISTENT_BRANCH_ID, PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    void 판매_숨기기시_상품이_존재하지_않으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());

        assertThatThrownBy(() -> operationService.hideSale(branchId, NON_EXISTENT_PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    void 판매_숨기기시_상품이_비활성이면_PRODUCT_NOT_ACTIVE_ERROR를_던진다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(PRODUCT_ID, branchId, false));

        assertThatThrownBy(() -> operationService.hideSale(branchId, PRODUCT_ID))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.PRODUCT_NOT_ACTIVE_ERROR));
    }

    @Test
    void 판매_숨기기중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(PRODUCT_ID, branchId, true));
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> operationService.hideSale(branchId, PRODUCT_ID))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── soldOut ──────────────────────────────────────────────────────────────

    @Test
    void 상품을_정상_품절_처리한다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(PRODUCT_ID, branchId, true));

        operationService.soldOut(branchId, PRODUCT_ID);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, PRODUCT_ID).orElseThrow();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.SOLD_OUT);
    }

    @Test
    void 이미_숨겨진_상품을_품절_처리하면_SOLD_OUT으로_상태가_변경된다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(PRODUCT_ID, branchId, true));
        productSalesOverrideRepository.put(ProductSalesOverride.of(PRODUCT_ID, branchId, ProductSalesOverrideStatus.HIDDEN, Instant.now()));

        operationService.soldOut(branchId, PRODUCT_ID);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, PRODUCT_ID).orElseThrow();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.SOLD_OUT);
    }

    @Test
    void 품절_처리시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> operationService.soldOut(NON_EXISTENT_BRANCH_ID, PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    void 품절_처리시_상품이_존재하지_않으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());

        assertThatThrownBy(() -> operationService.soldOut(branchId, NON_EXISTENT_PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    void 품절_처리시_상품이_비활성이면_PRODUCT_NOT_ACTIVE_ERROR를_던진다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(PRODUCT_ID, branchId, false));

        assertThatThrownBy(() -> operationService.soldOut(branchId, PRODUCT_ID))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.PRODUCT_NOT_ACTIVE_ERROR));
    }

    @Test
    void 품절_처리중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchFixture.id;
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(PRODUCT_ID, branchId, true));
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> operationService.soldOut(branchId, PRODUCT_ID))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── restoreSale ──────────────────────────────────────────────────────────

    @Test
    void 판매_재개를_정상_처리한다() {
        BranchId branchId = BranchFixture.id;
        productSalesOverrideRepository.put(ProductSalesOverride.of(PRODUCT_ID, branchId, ProductSalesOverrideStatus.HIDDEN, Instant.now()));

        operationService.restoreSale(branchId, PRODUCT_ID);

        assertThat(productSalesOverrideRepository.contains(branchId, PRODUCT_ID)).isFalse();
    }

    @Test
    void 판매_재개시_판매_재정의가_없으면_SALES_OVERRIDE_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> operationService.restoreSale(BranchFixture.id, PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.SALES_OVERRIDE_NOT_FOUND_ERROR));
    }

    @Test
    void 판매_재개중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        productSalesOverrideRepository.throwOnNextCall();

        assertThatThrownBy(() -> operationService.restoreSale(BranchFixture.id, PRODUCT_ID))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }
}
