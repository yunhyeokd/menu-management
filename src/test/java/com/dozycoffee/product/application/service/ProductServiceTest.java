package com.dozycoffee.product.application.service;

import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.ServiceError;
import com.dozycoffee.core.application.exception.*;
import com.dozycoffee.product.application.repository.*;
import com.dozycoffee.product.application.dto.*;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.product.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductServiceTest {

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
                () -> ProductId.of(nextProductId++)
        );
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private Category defaultCategory() {
        return categoryRepository.put(Category.of(CategoryId.of(1L), "음료", Instant.now()));
    }

    private void defaultBranch() {
        branchExistencePort.register(BranchId.of(1L));
    }

    // ─── register ────────────────────────────────────────────────────────────

    @Test
    public void 공통_상품을_정상_생성한다() {
        defaultCategory();
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductKind.COMMON, null,
                "아메리카노", null, null,
                CategoryId.of(1L), 3000, null, null,
                Set.of(), List.of()
        );

        Product result = productService.register(command);

        assertThat(result.getName()).isEqualTo("아메리카노");
        assertThat(result.getId()).isEqualTo(ProductId.of(1L));
        assertThat(productRepository.findById(ProductId.of(1L))).isPresent();
    }

    @Test
    public void 지점_전용_상품을_정상_생성한다() {
        defaultCategory();
        defaultBranch();
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductKind.BRANCH_EXCLUSIVE, BranchId.of(1L),
                "지점전용라떼", null, null,
                CategoryId.of(1L), 4500, null, null,
                Set.of(), List.of()
        );

        Product result = productService.register(command);

        assertThat(result.getName()).isEqualTo("지점전용라떼");
        assertThat(productRepository.findById(ProductId.of(1L))).isPresent();
    }

    @Test
    public void 상품_생성시_카테고리가_없으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        ProductRegisterCommand command = new ProductRegisterCommand(
                ProductKind.COMMON, null,
                "아메리카노", null, null,
                CategoryId.of(999L), 3000, null, null,
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
                ProductKind.BRANCH_EXCLUSIVE, BranchId.of(999L),
                "지점전용라떼", null, null,
                CategoryId.of(1L), 4500, null, null,
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
                CategoryId.of(1L), 3000, null, null,
                Set.of(), List.of()
        );

        assertThatThrownBy(() -> productService.register(command))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── searchProducts ───────────────────────────────────────────────────────

    @Test
    public void 상품_필터_조회를_정상_수행한다() {
        productQueryRepository.add(new ProductDetailResult(
                ProductId.of(1L), "아메리카노", null, null,
                new CategoryData(CategoryId.of(1L), "음료"),
                3000, null, null,
                ProductKind.COMMON, null, ProductStatus.ACTIVE,
                List.of(), List.of(), Instant.now()
        ));

        List<ProductDetailResult> results = productService.searchProducts(ProductFilterQuery.empty());

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
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        ProductProfileUpdateCommand command = new ProductProfileUpdateCommand(
                ProductId.of(1L), "라떼", null, null,
                CategoryId.of(1L), 4000, null, null, Set.of()
        );

        Product result = productService.updateProfile(command);

        assertThat(result.getName()).isEqualTo("라떼");
        assertThat(result.getPrice()).isEqualTo(4000);
    }

    @Test
    public void 상품_수정시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        defaultCategory();
        ProductProfileUpdateCommand command = new ProductProfileUpdateCommand(
                ProductId.of(999L), "라떼", null, null,
                CategoryId.of(1L), 4000, null, null, Set.of()
        );

        assertThatThrownBy(() -> productService.updateProfile(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    public void 상품_수정시_카테고리가_없으면_CATEGORY_NOT_FOUND_ERROR를_던진다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());
        ProductProfileUpdateCommand command = new ProductProfileUpdateCommand(
                ProductId.of(1L), "라떼", null, null,
                CategoryId.of(999L), 4000, null, null, Set.of()
        );

        assertThatThrownBy(() -> productService.updateProfile(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
    }

    // ─── deleteById ───────────────────────────────────────────────────────────

    @Test
    public void 상품을_정상_삭제한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null).build());

        productService.deleteById(ProductId.of(1L));

        assertThat(productRepository.findById(ProductId.of(1L))).isEmpty();
    }

    @Test
    public void 상품_삭제시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.deleteById(ProductId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    // ─── activate / deactivate ────────────────────────────────────────────────

    @Test
    public void 상품_상태를_활성으로_변경한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null)
                .status(ProductStatus.INACTIVE).build());

        productService.activate(ProductId.of(1L));

        assertThat(productRepository.findById(ProductId.of(1L)).get().getStatus())
                .isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    public void 상품_상태를_비활성으로_변경한다() {
        productRepository.put(ProductFixture.builder()
                .id(ProductId.of(1L)).kind(ProductKind.COMMON).branchId(null)
                .status(ProductStatus.ACTIVE).build());

        productService.deactivate(ProductId.of(1L));

        assertThat(productRepository.findById(ProductId.of(1L)).get().getStatus())
                .isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    public void 상품_활성화시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.activate(ProductId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    public void 상품_비활성화시_상품이_없으면_PRODUCT_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productService.deactivate(ProductId.of(999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }
}
