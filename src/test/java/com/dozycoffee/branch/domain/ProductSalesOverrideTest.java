package com.dozycoffee.branch.domain;

import com.dozycoffee.product.domain.ProductFixture;
import com.dozycoffee.product.domain.ProductId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductSalesOverrideTest {

    @Test
    public void 지점상품_오버라이드를_정상_생성한다() {

        ProductId productId = ProductFixture.Defaults.id;
        BranchId branchId = BranchFixture.id;
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;

        ProductSalesOverride salesOverride = ProductSalesOverride.create(productId, branchId, status);
        assertThat(salesOverride.getProductId()).isEqualTo(productId);
        assertThat(salesOverride.getBranchId()).isEqualTo(branchId);
        assertThat(salesOverride.getStatus()).isEqualTo(status);
        assertThat(salesOverride.getCreatedAt()).isNotNull();
    }

    @Test
    public void 지점상품_오버라이드_상태가_null이면_예외를_발생시킨다() {
        ProductId productId = ProductFixture.Defaults.id;
        BranchId branchId = BranchFixture.id;

        assertThatThrownBy(() -> ProductSalesOverride.create(productId, branchId, null))
                .isInstanceOf(BranchException.class);
    }

    @Test
    public void 복합키가_같은_지점상품_오버라이드는_동등하다() {
        ProductId productId = ProductFixture.Defaults.id;
        BranchId branchId = BranchFixture.id;
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;
        Instant createdAt = Instant.now();

        ProductSalesOverride salesOverride1 = ProductSalesOverride.of(productId, branchId, status, createdAt);
        ProductSalesOverride salesOverride2 = ProductSalesOverride.of(productId, branchId, status, createdAt);

        assertThat(salesOverride1).isEqualTo(salesOverride2);
    }

    @Test
    public void productId가_다른_지점상품_오버라이드는_동등하지_않다() {
        BranchId branchId = BranchFixture.id;
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;
        Instant createdAt = Instant.now();

        ProductSalesOverride salesOverride1 = ProductSalesOverride.of(ProductFixture.Defaults.id, branchId, status, createdAt);
        ProductSalesOverride salesOverride2 = ProductSalesOverride.of(ProductId.of("00000000-0000-0000-0000-000000000002"), branchId, status, createdAt);

        assertThat(salesOverride1).isNotEqualTo(salesOverride2);
    }

    @Test
    public void branchId가_다른_지점상품_오버라이드는_동등하지_않다() {
        ProductId productId = ProductFixture.Defaults.id;
        ProductSalesOverrideStatus status = ProductSalesOverrideStatus.SOLD_OUT;
        Instant createdAt = Instant.now();

        ProductSalesOverride salesOverride1 = ProductSalesOverride.of(productId, BranchFixture.id, status, createdAt);
        ProductSalesOverride salesOverride2 = ProductSalesOverride.of(productId, BranchId.of("00000000-0000-0000-0000-000000000002"), status, createdAt);

        assertThat(salesOverride1).isNotEqualTo(salesOverride2);
    }

}
