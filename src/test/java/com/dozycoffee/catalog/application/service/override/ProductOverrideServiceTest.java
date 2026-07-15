package com.dozycoffee.catalog.application.service.override;

import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.catalog.application.repository.FakeBranchExistencePort;
import com.dozycoffee.catalog.application.repository.FakeProductRepository;
import com.dozycoffee.catalog.application.repository.FakeProductSalesOverrideRepository;
import com.dozycoffee.catalog.application.service.ProductErrors;
import com.dozycoffee.catalog.domain.Product;
import com.dozycoffee.catalog.domain.ProductFixture;
import com.dozycoffee.catalog.domain.ProductId;
import com.dozycoffee.catalog.domain.ProductKind;
import com.dozycoffee.catalog.domain.ProductStatus;
import com.dozycoffee.catalog.domain.override.ProductSalesOverride;
import com.dozycoffee.catalog.domain.override.ProductSalesOverrideStatus;
import com.dozycoffee.core.exception.service.ConflictException;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.dozycoffee.core.exception.service.ServiceError;
import com.dozycoffee.core.exception.service.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductOverrideServiceTest {

    private static final BranchId BRANCH_ID = BranchId.of("00000000-0000-0000-0000-000000000001");
    private static final ProductId NON_EXISTENT_PRODUCT_ID = ProductId.of("00000000-0000-0000-0000-000000000999");
    private static final BranchId NON_EXISTENT_BRANCH_ID = BranchId.of("00000000-0000-0000-0000-000000000999");

    private FakeProductRepository productRepository;
    private FakeProductSalesOverrideRepository productSalesOverrideRepository;
    private FakeBranchExistencePort branchExistencePort;
    private ProductOverrideService productOverrideService;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        productSalesOverrideRepository = new FakeProductSalesOverrideRepository();
        branchExistencePort = new FakeBranchExistencePort();
        branchExistencePort.register(BRANCH_ID);
        productOverrideService = new ProductOverrideService(branchExistencePort, productRepository, productSalesOverrideRepository);
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((ServiceException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private Product activeProduct() {
        return productRepository.put(ProductFixture.builder()
                .kind(ProductKind.COMMON).branchId(null).status(ProductStatus.ACTIVE).build());
    }

    @Test
    void 지점이_존재하지_않으면_숨김처리시_예외가_발생한다() {
        Product product = activeProduct();

        assertThatThrownBy(() -> productOverrideService.hideSale(NON_EXISTENT_BRANCH_ID, product.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.BRANCH_NOT_FOUND_ERROR));
    }

    @Test
    void 존재하지_않는_상품을_숨김처리하면_예외가_발생한다() {
        assertThatThrownBy(() -> productOverrideService.hideSale(BRANCH_ID, NON_EXISTENT_PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    @Test
    void 비활성_상품을_숨김처리하면_예외가_발생한다() {
        Product inactive = productRepository.put(ProductFixture.builder()
                .kind(ProductKind.COMMON).branchId(null).status(ProductStatus.INACTIVE).build());

        assertThatThrownBy(() -> productOverrideService.hideSale(BRANCH_ID, inactive.getId()))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.PRODUCT_NOT_ACTIVE_ERROR));
    }

    @Test
    void 상품을_숨김처리하면_오버라이드가_생성된다() {
        Product product = activeProduct();

        productOverrideService.hideSale(BRANCH_ID, product.getId());

        Optional<ProductSalesOverride> override = productSalesOverrideRepository.findByBranchIdAndProductId(BRANCH_ID, product.getId());
        assertThat(override).isPresent();
        assertThat(override.get().getStatus()).isEqualTo(ProductSalesOverrideStatus.HIDDEN);
    }

    @Test
    void 상품을_품절처리하면_오버라이드가_생성된다() {
        Product product = activeProduct();

        productOverrideService.soldOut(BRANCH_ID, product.getId());

        Optional<ProductSalesOverride> override = productSalesOverrideRepository.findByBranchIdAndProductId(BRANCH_ID, product.getId());
        assertThat(override).isPresent();
        assertThat(override.get().getStatus()).isEqualTo(ProductSalesOverrideStatus.SOLD_OUT);
    }

    @Test
    void 품절_상태인_상품을_숨김처리하면_상태가_교체된다() {
        Product product = activeProduct();
        productOverrideService.soldOut(BRANCH_ID, product.getId());

        productOverrideService.hideSale(BRANCH_ID, product.getId());

        Optional<ProductSalesOverride> override = productSalesOverrideRepository.findByBranchIdAndProductId(BRANCH_ID, product.getId());
        assertThat(override).isPresent();
        assertThat(override.get().getStatus()).isEqualTo(ProductSalesOverrideStatus.HIDDEN);
    }

    @Test
    void 오버라이드가_없는_상품을_원복하면_예외가_발생한다() {
        Product product = activeProduct();

        assertThatThrownBy(() -> productOverrideService.restoreSale(BRANCH_ID, product.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.SALES_OVERRIDE_NOT_FOUND_ERROR));
    }

    @Test
    void 오버라이드된_상품을_원복하면_오버라이드가_삭제된다() {
        Product product = activeProduct();
        productOverrideService.hideSale(BRANCH_ID, product.getId());

        productOverrideService.restoreSale(BRANCH_ID, product.getId());

        assertThat(productSalesOverrideRepository.contains(BRANCH_ID, product.getId())).isFalse();
    }
}
