package com.dozycoffee.product.application.service;

import com.dozycoffee.core.exception.base.*;
import com.dozycoffee.core.exception.service.*;
import com.dozycoffee.product.application.repository.*;
import com.dozycoffee.product.application.dto.*;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductServiceTest {

    private static final ProductId NON_EXISTENT_PRODUCT_ID = ProductId.of("00000000-0000-0000-0000-000000000999");
    private static final CategoryId NON_EXISTENT_CATEGORY_ID = CategoryId.of("00000000-0000-0000-0000-000000000999");
    private static final BranchId NON_EXISTENT_BRANCH_ID = BranchId.of("00000000-0000-0000-0000-000000000999");

    private FakeProductRepository productRepository;
    private FakeProductQueryRepository productQueryRepository;
    private FakeCategoryRepository categoryRepository;
    private FakeBranchExistencePort branchExistencePort;
    private ProductService productService;

    private long nextProductId = 1L;

    @BeforeEach
    public void setUp() {
        productRepository = new FakeProductRepository();
        productQueryRepository = new FakeProductQueryRepository();
        categoryRepository = new FakeCategoryRepository();
        branchExistencePort = new FakeBranchExistencePort();
        nextProductId = 1L;

        productService = new ProductService(
                productRepository,
                productQueryRepository,
                categoryRepository,
                branchExistencePort,
                () -> ProductId.of(String.format("00000000-0000-0000-0000-%012d", nextProductId++))
        );
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((ServiceException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private Category defaultCategory() {
        return categoryRepository.put(CategoryFixture.builder().name("음료").build());
    }

    private void defaultBranch() {
        branchExistencePort.register(BranchId.of("00000000-0000-0000-0000-000000000001"));
    }

    // ─── register ────────────────────────────────────────────────────────────

    @Test
    public void 공통_상품을_정상_생성한다() {
        defaultCategory();
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductKind.COMMON, null,
                "아메리카노", null, null,
                CategoryFixture.Defaults.id, 3000, null, null,
                Set.of(), List.of()
        );

        Product result = productService.register(command);

        assertThat(result.getName()).isEqualTo("아메리카노");
        assertThat(result.getId()).isEqualTo(ProductFixture.Defaults.id);
        assertThat(productRepository.findById(ProductFixture.Defaults.id)).isPresent();
    }

    @Test
    public void 지점_전용_상품을_정상_생성한다() {
        defaultCategory();
        defaultBranch();
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductKind.BRANCH_EXCLUSIVE, BranchId.of("00000000-0000-0000-0000-000000000001"),
                "지점전용라떼", null, null,
                CategoryFixture.Defaults.id, 4500, null, null,
                Set.of(), List.of()
        );

        Product result = productService.register(command);

        assertThat(result.getName()).isEqualTo("지점전용라떼");
        assertThat(productRepository.findById(ProductFixture.Defaults.id)).isPresent();
    }

    @Test
    public void 상품_생성시_카테고리가_없으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductKind.COMMON, null,
                "아메리카노", null, null,
                NON_EXISTENT_CATEGORY_ID, 3000, null, null,
                Set.of(), List.of()
        );

        assertThatThrownBy(() -> productService.register(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    @Test
    public void 지점_전용_상품_생성시_지점이_없으면_BRANCH_NOT_FOUND_ERROR를_던진다() {
        defaultCategory();
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductKind.BRANCH_EXCLUSIVE, NON_EXISTENT_BRANCH_ID,
                "지점전용라떼", null, null,
                CategoryFixture.Defaults.id, 4500, null, null,
                Set.of(), List.of()
        );

        assertThatThrownBy(() -> productService.register(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    public void 상품_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        defaultCategory();
        productRepository.throwOnNextCall();
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductKind.COMMON, null,
                "아메리카노", null, null,
                CategoryFixture.Defaults.id, 3000, null, null,
                Set.of(), List.of()
        );

        assertThatThrownBy(() -> productService.register(command))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── searchProducts ───────────────────────────────────────────────────────

    @Test
    public void 상품_필터_조회를_정상_수행한다() {
        productQueryRepository.add(new ProductSummaryResult(
                ProductFixture.Defaults.id, "아메리카노", null,
                new CategoryData(CategoryFixture.Defaults.id, "음료"),
                3000, ProductKind.COMMON, null, ProductStatus.ACTIVE, List.of()
        ));

        List<ProductSummaryResult> results = productService.searchProducts(ProductFilterQuery.empty());

        assertThat(results).hasSize(1);
    }

    @Test
    public void 상품_필터_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        productQueryRepository.throwOnNextCall();

        assertThatThrownBy(() -> productService.searchProducts(ProductFilterQuery.empty()))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── updateProfile ────────────────────────────────────────────────────────

    @Test
    public void 공통_상품_프로필을_정상_수정한다() {
        defaultCategory();
        ProductId productId = ProductFixture.Defaults.id;
        productRepository.put(ProductFixture.builder()
                .id(productId).kind(ProductKind.COMMON).branchId(null).build());
        ProductProfileUpdateCommand command = new ProductProfileUpdateCommand(
                "라떼", null, null,
                CategoryFixture.Defaults.id, 4000, null, null, Set.of()
        );

        Product result = productService.updateProfile(productId, command);

        assertThat(result.getName()).isEqualTo("라떼");
        assertThat(result.getPrice()).isEqualTo(4000);
    }

    @Test
    public void 상품_수정시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        defaultCategory();
        ProductProfileUpdateCommand command = new ProductProfileUpdateCommand(
                "라떼", null, null,
                CategoryFixture.Defaults.id, 4000, null, null, Set.of()
        );

        assertThatThrownBy(() -> productService.updateProfile(NON_EXISTENT_PRODUCT_ID, command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    public void 상품_수정시_카테고리가_없으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        ProductId productId = ProductFixture.Defaults.id;
        productRepository.put(ProductFixture.builder()
                .id(productId).kind(ProductKind.COMMON).branchId(null).build());
        ProductProfileUpdateCommand command = new ProductProfileUpdateCommand(
                "라떼", null, null,
                NON_EXISTENT_CATEGORY_ID, 4000, null, null, Set.of()
        );

        assertThatThrownBy(() -> productService.updateProfile(productId, command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    // ─── deleteById ───────────────────────────────────────────────────────────

    @Test
    public void 상품을_정상_삭제한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductFixture.Defaults.id).kind(ProductKind.COMMON).branchId(null).build());

        productService.deleteById(ProductFixture.Defaults.id);

        assertThat(productRepository.findById(ProductFixture.Defaults.id)).isEmpty();
    }

    @Test
    public void 상품_삭제시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.deleteById(NON_EXISTENT_PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    // ─── activate / deactivate ────────────────────────────────────────────────

    @Test
    public void 상품_상태를_활성으로_변경한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductFixture.Defaults.id).kind(ProductKind.COMMON).branchId(null)
                .status(ProductStatus.INACTIVE).build());

        productService.activate(ProductFixture.Defaults.id);

        assertThat(productRepository.findById(ProductFixture.Defaults.id).get().getStatus())
                .isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    public void 상품_상태를_비활성으로_변경한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductFixture.Defaults.id).kind(ProductKind.COMMON).branchId(null)
                .status(ProductStatus.ACTIVE).build());

        productService.deactivate(ProductFixture.Defaults.id);

        assertThat(productRepository.findById(ProductFixture.Defaults.id).get().getStatus())
                .isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    public void 상품_활성화시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.activate(NON_EXISTENT_PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    public void 상품_비활성화시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.deactivate(NON_EXISTENT_PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }
}
