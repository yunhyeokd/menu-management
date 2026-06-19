package com.dozycoffee.domain.product;

import com.dozycoffee.domain.branch.BranchId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductTest {

    @Test
    public void 공통_상품을_정상_생성한다() {
        Product product = ProductFixture.builder()
                .name(ProductFixture.Base.name)
                .description(ProductFixture.Base.description)
                .imageUrl(ProductFixture.Base.imageUrl)
                .categoryId(ProductFixture.Base.categoryId)
                .price(ProductFixture.Base.price)
                .kcal(ProductFixture.Base.kcal)
                .allergenInfo(ProductFixture.Base.allergenInfo)
                .createCommonProduct();

        assertThat(product.getName()).isEqualTo(ProductFixture.Base.name);
        assertThat(product.getDescription()).isEqualTo(ProductFixture.Base.description);
        assertThat(product.getImageUrl()).isEqualTo(ProductFixture.Base.imageUrl);
        assertThat(product.getCategoryId()).isEqualTo(ProductFixture.Base.categoryId);
        assertThat(product.getPrice()).isEqualTo(ProductFixture.Base.price);
        assertThat(product.getKcal()).isEqualTo(ProductFixture.Base.kcal);
        assertThat(product.getAllergenInfo()).isEqualTo(ProductFixture.Base.allergenInfo);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    public void 지점_전용_상품을_정상_생성한다() {
        Product product = ProductFixture.builder()
                .name(ProductFixture.Base.name)
                .description(ProductFixture.Base.description)
                .imageUrl(ProductFixture.Base.imageUrl)
                .categoryId(ProductFixture.Base.categoryId)
                .price(ProductFixture.Base.price)
                .kcal(ProductFixture.Base.kcal)
                .allergenInfo(ProductFixture.Base.allergenInfo)
                .branchId(ProductFixture.Base.branchId)
                .createBranchProduct();

        assertThat(product.getName()).isEqualTo(ProductFixture.Base.name);
        assertThat(product.getDescription()).isEqualTo(ProductFixture.Base.description);
        assertThat(product.getImageUrl()).isEqualTo(ProductFixture.Base.imageUrl);
        assertThat(product.getCategoryId()).isEqualTo(ProductFixture.Base.categoryId);
        assertThat(product.getPrice()).isEqualTo(ProductFixture.Base.price);
        assertThat(product.getKcal()).isEqualTo(ProductFixture.Base.kcal);
        assertThat(product.getAllergenInfo()).isEqualTo(ProductFixture.Base.allergenInfo);
        assertThat(product.getBranchId()).isEqualTo(ProductFixture.Base.branchId);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "", " ",
            "#product", " product "
    })
    public void 상품_생성시_상품명이_유효하지_않으면_예외가_발생한다(String invalidName) {
        ProductFixture.Builder builder = ProductFixture.builder().name(invalidName);

        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_생성시_상품명이_최대_길이보다_크면_예외가_발생한다() {
        ProductFixture.Builder builder = ProductFixture.builder().name("a".repeat(101));

        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_생성시_상품설명이_최대_길이보다_크면_예외가_발생한다() {
        ProductFixture.Builder builder = ProductFixture.builder().description("a".repeat(1001));
        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", " ",
            "http://example.com", " https://example.com", "abchttps://example.com",
    })
    public void 상품_생성시_상품이미지_URL이_유효하지_않으면_예외가_발생한다(String invalidUrl) {
        ProductFixture.Builder builder = ProductFixture.builder().imageUrl(invalidUrl);

        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_생성시_상품이미지_URL이_최대_길이보다_크면_예외가_발생한다() {
        ProductFixture.Builder builder = ProductFixture.builder().imageUrl("https://example.com/" + "a".repeat(981));
        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_생성시_상품_가격이_0보다_작으면_예외가_발생한다() {
        ProductFixture.Builder builder = ProductFixture.builder().price(-1);
        assertThatThrownBy(builder::createCommonProduct).isInstanceOf(ProductException.class);
        assertThatThrownBy(builder::createBranchProduct).isInstanceOf(ProductException.class);
    }

    @Test
    public void id가_같은_상품은_동등하다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product1 = builder.id(ProductId.of(1L)).build();
        Product product2 = builder.id(ProductId.of(1L)).build();
        assertThat(product1).isEqualTo(product2);
    }

    @Test
    public void id가_다른_상품은_동등하지_않다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product1 = builder.id(ProductId.of(1L)).build();
        Product product2 = builder.id(ProductId.of(2L)).build();
        assertThat(product1).isNotEqualTo(product2);
    }



    @Test
    public void id가_null인_상품은_동등하지_않다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product1 = builder.id(ProductId.of(1L)).build();
        Product product2 = builder.id(ProductId.of(2L)).createCommonProduct();
        assertThat(product1).isNotEqualTo(product2);
    }


    @Test
    public void 카테고리를_제거한_상품은_비활성화된다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product = builder.createCommonProduct();
        product.removeCategory();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.getCategoryId()).isNull();
    }

    @Test
    public void 카테고리가_없는_상품은_활성화할_수_없다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product = builder.status(ProductStatus.INACTIVE).categoryId(null).build();
        assertThatThrownBy(product::activate).isInstanceOf(ProductException.class);
    }

    @Test
    public void 공통_상품에_branchId가_있으면_예외가_발생한다() {
        assertThatThrownBy(() -> ProductFixture.builder()
                .kind(ProductKind.COMMON)
                .branchId(BranchId.of(1L))
                .build()
        ).isInstanceOf(ProductException.class);
    }

    @Test
    public void 지점_전용_상품에_branchId가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> ProductFixture.builder()
                .kind(ProductKind.BRANCH_EXCLUSIVE)
                .branchId(null)
                .build()
        ).isInstanceOf(ProductException.class);
    }

    @Test
    public void categoryId가_없는_상품이_활성_상태이면_예외가_발생한다() {
        assertThatThrownBy(() -> ProductFixture.builder()
                .categoryId(null)
                .status(ProductStatus.ACTIVE)
                .build()
        ).isInstanceOf(ProductException.class);
    }

    @Test
    public void 상품_가격을_0보다_작게_변경할_수_없다() {
        ProductFixture.Builder builder = ProductFixture.builder();
        Product product = builder.createCommonProduct();
        assertThatThrownBy(() -> product.updatePrice(-1))
                .isInstanceOf(ProductException.class);
    }


}
