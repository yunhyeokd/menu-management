package com.dozycoffee.domain.product;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductOptionGroupTest {

    @Test
    public void 상품_옵션그룹을_정상_생성한다() {
        long productId = 1L;
        long optionGroupId = 1L;
        boolean isRequired = true;
        boolean allowMultiple = false;

        ProductOptionGroup productOptionGroup = ProductOptionGroup.create(productId, optionGroupId, isRequired, allowMultiple);

        assertThat(productId).isEqualTo(productOptionGroup.getProductId());
        assertThat(optionGroupId).isEqualTo(productOptionGroup.getOptionGroupId());
        assertThat(isRequired).isEqualTo(productOptionGroup.isRequired());
        assertThat(allowMultiple).isEqualTo(productOptionGroup.isAllowMultiple());
        assertThat(productOptionGroup.getCreatedAt()).isNotNull();
    }

    @Test
    public void id가_같은_상품_옵션그룹은_동등하다() {
        long productId = 1L;
        long optionGroupId = 1L;
        boolean isRequired = true;
        boolean allowMultiple = false;
        Instant createdAt = Instant.now();

        ProductOptionGroup productOptionGroup1 = ProductOptionGroup.of(1L, productId, optionGroupId, isRequired, allowMultiple, createdAt);
        ProductOptionGroup productOptionGroup2 = ProductOptionGroup.of(1L, productId, optionGroupId, isRequired, allowMultiple, createdAt);

        assertThat(productOptionGroup1.getId()).isEqualTo(productOptionGroup2.getId());
        assertThat(productOptionGroup1).isEqualTo(productOptionGroup2);
    }

    @Test
    public void id가_다른_상품_옵션그룹은_동등하지_않다() {
        long productId = 1L;
        long optionGroupId = 1L;
        boolean isRequired = true;
        boolean allowMultiple = false;
        Instant createdAt = Instant.now();

        ProductOptionGroup productOptionGroup1 = ProductOptionGroup.of(1L, productId, optionGroupId, isRequired, allowMultiple, createdAt);
        ProductOptionGroup productOptionGroup2 = ProductOptionGroup.of(2L, productId, optionGroupId, isRequired, allowMultiple, createdAt);

        assertThat(productOptionGroup1.getId()).isNotEqualTo(productOptionGroup2.getId());
        assertThat(productOptionGroup1).isNotEqualTo(productOptionGroup2);
    }

    @Test
    public void id가_null인_상품_옵션그룹은_동등하지_않다() {
        long productId = 1L;
        long optionGroupId = 1L;
        boolean isRequired = true;
        boolean allowMultiple = false;
        Instant createdAt = Instant.now();

        ProductOptionGroup productOptionGroup1 = ProductOptionGroup.of(1L, productId, optionGroupId, isRequired, allowMultiple, createdAt);
        ProductOptionGroup productOptionGroup2 = ProductOptionGroup.create(productId, optionGroupId, isRequired, allowMultiple);

        assertThat(productOptionGroup1.getId()).isNotEqualTo(productOptionGroup2.getId());
        assertThat(productOptionGroup1).isNotEqualTo(productOptionGroup2);
    }

}
