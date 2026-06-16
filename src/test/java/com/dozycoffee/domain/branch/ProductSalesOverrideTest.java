package com.dozycoffee.domain.branch;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductSalesOverrideTest {

    @Test
    public void 지점상품_오버라이드를_정상_생성한다() {

        long productId = 1L;
        long branchId = 1L;
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;

        ProductSalesOverride salesOverride = ProductSalesOverride.create(productId, branchId, status);
        assertThat(salesOverride.getProductId()).isEqualTo(productId);
        assertThat(salesOverride.getBranchId()).isEqualTo(branchId);
        assertThat(salesOverride.getStatus()).isEqualTo(status);
    }

    @Test
    public void 지점상품_오버라이드_상태가_null이면_예외를_발생시킨다() {
        long productId = 1L;
        long branchId = 1L;

        assertThatThrownBy(() -> ProductSalesOverride.create(productId, branchId, null))
                .isInstanceOf(BranchException.class);
    }

    @Test
    public void id가_같은_지점상품_오버라이드는_동등하다() {
        long productId = 1L;
        long branchId = 1L;
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;
        Instant createdAt = Instant.now();

        ProductSalesOverride salesOverride1 = ProductSalesOverride.of(1L, productId, branchId, status, createdAt);
        ProductSalesOverride salesOverride2 = ProductSalesOverride.of(1L, productId, branchId, status, createdAt);

        assertThat(salesOverride1.getId()).isEqualTo(salesOverride2.getId());
        assertThat(salesOverride1).isEqualTo(salesOverride2);
    }

    @Test
    public void id가_다른_지점상품_오버라이드는_동등하지_않다() {
        long productId = 1L;
        long branchId = 1L;

        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;
        Instant createdAt = Instant.now();


        ProductSalesOverride salesOverride1 = ProductSalesOverride.of(1L, productId, branchId, status, createdAt);
        ProductSalesOverride salesOverride2 = ProductSalesOverride.of(2L, productId, branchId, status, createdAt);

        assertThat(salesOverride1.getId()).isNotEqualTo(salesOverride2.getId());
        assertThat(salesOverride1).isNotEqualTo(salesOverride2);
    }

    @Test
    public void id가_null인_지점상품_오버라이드는_동등하지_않다() {
        long productId = 1L;
        long branchId = 1L;

        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;
        Instant createdAt = Instant.now();

        ProductSalesOverride salesOverride1 = ProductSalesOverride.of(1L, productId, branchId, status, createdAt);
        ProductSalesOverride salesOverride2 = ProductSalesOverride.create(productId, branchId, status);

        assertThat(salesOverride1.getId()).isNotEqualTo(salesOverride2.getId());
        assertThat(salesOverride1).isNotEqualTo(salesOverride2);
    }

}
