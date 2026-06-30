package com.dozycoffee.branch.application;

import com.dozycoffee.branch.application.model.BranchProduct;
import com.dozycoffee.branch.domain.*;
import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.ServiceError;
import com.dozycoffee.core.application.exception.*;
import com.dozycoffee.product.domain.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BranchOperationServiceTest {

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
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    // ─── findOverridableProducts ──────────────────────────────────────────────

    @Test
    void 재정의_가능한_상품_목록을_조회한다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
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
        assertThatThrownBy(() -> operationService.findOverridableProducts(BranchId.of("00000000-0000-0000-0000-000000000999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    void 재정의_가능한_상품_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> operationService.findOverridableProducts(branchId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── hideSale ─────────────────────────────────────────────────────────────

    @Test
    void 상품_판매를_정상_숨긴다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        ProductId productId = ProductId.of("00000000-0000-0000-0000-000000000010");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(productId, branchId, true));

        operationService.hideSale(branchId, productId);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId).orElseThrow();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.HIDDEN);
    }

    @Test
    void 이미_품절_처리된_상품을_숨기면_HIDDEN으로_상태가_변경된다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        ProductId productId = ProductId.of("00000000-0000-0000-0000-000000000010");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(productId, branchId, true));
        productSalesOverrideRepository.put(ProductSalesOverride.of(productId, branchId, ProductSalesOverrideStatus.SOLD_OUT, Instant.now()));

        operationService.hideSale(branchId, productId);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId).orElseThrow();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.HIDDEN);
    }

    @Test
    void 판매_숨기기시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> operationService.hideSale(BranchId.of("00000000-0000-0000-0000-000000000999"), ProductId.of("00000000-0000-0000-0000-000000000010")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    void 판매_숨기기시_상품이_존재하지_않으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());

        assertThatThrownBy(() -> operationService.hideSale(branchId, ProductId.of("00000000-0000-0000-0000-000000000999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    void 판매_숨기기시_상품이_비활성이면_PRODUCT_NOT_ACTIVE_ERROR를_던진다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        ProductId productId = ProductId.of("00000000-0000-0000-0000-000000000010");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(productId, branchId, false));

        assertThatThrownBy(() -> operationService.hideSale(branchId, productId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.PRODUCT_NOT_ACTIVE_ERROR));
    }

    @Test
    void 판매_숨기기중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        ProductId productId = ProductId.of("00000000-0000-0000-0000-000000000010");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(productId, branchId, true));
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> operationService.hideSale(branchId, productId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── soldOut ──────────────────────────────────────────────────────────────

    @Test
    void 상품을_정상_품절_처리한다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        ProductId productId = ProductId.of("00000000-0000-0000-0000-000000000010");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(productId, branchId, true));

        operationService.soldOut(branchId, productId);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId).orElseThrow();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.SOLD_OUT);
    }

    @Test
    void 이미_숨겨진_상품을_품절_처리하면_SOLD_OUT으로_상태가_변경된다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        ProductId productId = ProductId.of("00000000-0000-0000-0000-000000000010");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(productId, branchId, true));
        productSalesOverrideRepository.put(ProductSalesOverride.of(productId, branchId, ProductSalesOverrideStatus.HIDDEN, Instant.now()));

        operationService.soldOut(branchId, productId);

        ProductSalesOverride override = productSalesOverrideRepository.findByBranchIdAndProductId(branchId, productId).orElseThrow();
        assertThat(override.getStatus()).isEqualTo(ProductSalesOverrideStatus.SOLD_OUT);
    }

    @Test
    void 품절_처리시_지점이_존재하지_않으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> operationService.soldOut(BranchId.of("00000000-0000-0000-0000-000000000999"), ProductId.of("00000000-0000-0000-0000-000000000010")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    void 품절_처리시_상품이_존재하지_않으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());

        assertThatThrownBy(() -> operationService.soldOut(branchId, ProductId.of("00000000-0000-0000-0000-000000000999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    void 품절_처리시_상품이_비활성이면_PRODUCT_NOT_ACTIVE_ERROR를_던진다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        ProductId productId = ProductId.of("00000000-0000-0000-0000-000000000010");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(productId, branchId, false));

        assertThatThrownBy(() -> operationService.soldOut(branchId, productId))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.PRODUCT_NOT_ACTIVE_ERROR));
    }

    @Test
    void 품절_처리중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        ProductId productId = ProductId.of("00000000-0000-0000-0000-000000000010");
        branchAccountRepository.put(BranchFixture.builder().id(branchId).build());
        productQueryPort.put(BranchProduct.of(productId, branchId, true));
        branchAccountRepository.throwOnNextCall();

        assertThatThrownBy(() -> operationService.soldOut(branchId, productId))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }

    // ─── restoreSale ──────────────────────────────────────────────────────────

    @Test
    void 판매_재개를_정상_처리한다() {
        BranchId branchId = BranchId.of("00000000-0000-0000-0000-000000000001");
        ProductId productId = ProductId.of("00000000-0000-0000-0000-000000000010");
        productSalesOverrideRepository.put(ProductSalesOverride.of(productId, branchId, ProductSalesOverrideStatus.HIDDEN, Instant.now()));

        operationService.restoreSale(branchId, productId);

        assertThat(productSalesOverrideRepository.contains(branchId, productId)).isFalse();
    }

    @Test
    void 판매_재개시_판매_재정의가_없으면_SALES_OVERRIDE_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> operationService.restoreSale(BranchId.of("00000000-0000-0000-0000-000000000001"), ProductId.of("00000000-0000-0000-0000-000000000010")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.SALES_OVERRIDE_NOT_FOUND_ERROR));
    }

    @Test
    void 판매_재개중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        productSalesOverrideRepository.throwOnNextCall();

        assertThatThrownBy(() -> operationService.restoreSale(BranchId.of("00000000-0000-0000-0000-000000000001"), ProductId.of("00000000-0000-0000-0000-000000000010")))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, BranchErrors.UNKNOWN_ERROR));
    }
}
