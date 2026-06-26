package com.dozycoffee.domain.branch;

import com.dozycoffee.domain.product.ProductId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductSalesOverrideTest {

    @Test
    public void 지점상품_오버라이드를_정상_생성한다() {

        ProductId productId = ProductId.of(1L);
        BranchId branchId = BranchId.of(1L);
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;

        ProductSalesOverride salesOverride = ProductSalesOverride.create(productId, branchId, status);
        assertThat(salesOverride.getProductId()).isEqualTo(productId);
        assertThat(salesOverride.getBranchId()).isEqualTo(branchId);
        assertThat(salesOverride.getStatus()).isEqualTo(status);
        assertThat(salesOverride.getCreatedAt()).isNotNull();
    }

    @Test
    public void 지점상품_오버라이드_상태가_null이면_예외를_발생시킨다() {
        ProductId productId = ProductId.of(1L);
        BranchId branchId = BranchId.of(1L);

        assertThatThrownBy(() -> ProductSalesOverride.create(productId, branchId, null))
                .isInstanceOf(BranchException.class);
    }

    @Test
    public void 복합키가_같은_지점상품_오버라이드는_동등하다() {
        ProductId productId = ProductId.of(1L);
        BranchId branchId = BranchId.of(1L);
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;
        Instant createdAt = Instant.now();

        ProductSalesOverride salesOverride1 = ProductSalesOverride.of(productId, branchId, status, createdAt);
        ProductSalesOverride salesOverride2 = ProductSalesOverride.of(productId, branchId, status, createdAt);

        assertThat(salesOverride1).isEqualTo(salesOverride2);
    }

    @Test
    public void productId가_다른_지점상품_오버라이드는_동등하지_않다() {
        BranchId branchId = BranchId.of(1L);
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;
        Instant createdAt = Instant.now();

        ProductSalesOverride salesOverride1 = ProductSalesOverride.of(ProductId.of(1L), branchId, status, createdAt);
        ProductSalesOverride salesOverride2 = ProductSalesOverride.of(ProductId.of(2L), branchId, status, createdAt);

        assertThat(salesOverride1).isNotEqualTo(salesOverride2);
    }

    @Test
    public void branchId가_다른_지점상품_오버라이드는_동등하지_않다() {
        ProductId productId = ProductId.of(1L);
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;
        Instant createdAt = Instant.now();

        ProductSalesOverride salesOverride1 = ProductSalesOverride.of(productId, BranchId.of(1L), status, createdAt);
        ProductSalesOverride salesOverride2 = ProductSalesOverride.of(productId, BranchId.of(2L), status, createdAt);

        assertThat(salesOverride1).isNotEqualTo(salesOverride2);
    }

}
