package com.dozycoffee.product.application.service.option;

import com.dozycoffee.core.exception.service.ServiceException;
import com.dozycoffee.core.exception.service.ServiceError;
import com.dozycoffee.core.exception.service.ConflictException;
import com.dozycoffee.core.exception.service.ResourceNotFoundException;
import com.dozycoffee.core.exception.service.SystemException;
import com.dozycoffee.product.application.dto.OptionGroupLinkCommand;
import com.dozycoffee.product.application.repository.FakeOptionGroupRepository;
import com.dozycoffee.product.application.repository.FakeProductOptionGroupRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductOptionGroupServiceTest {

    private FakeProductOptionGroupRepository productOptionGroupRepository;
    private FakeOptionGroupRepository optionGroupRepository;
    private ProductOptionGroupService productOptionGroupService;

    @BeforeEach
    void setUp() {
        productOptionGroupRepository = new FakeProductOptionGroupRepository();
        optionGroupRepository = new FakeOptionGroupRepository();
        productOptionGroupService = new ProductOptionGroupService(productOptionGroupRepository, optionGroupRepository);
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((ServiceException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private OptionGroup savedOptionGroup(long id) {
        OptionGroupId optionGroupId = OptionGroupId.of(String.format("00000000-0000-0000-0000-%012d", id));
        return optionGroupRepository.put(OptionGroupFixture.builder()
                .id(optionGroupId)
                .name("옵션그룹" + id)
                .build());
    }

    @Test
    void 상품에_옵션그룹을_연결한다() {
        ProductId productId = ProductFixture.Defaults.id;
        OptionGroup og = savedOptionGroup(10L);

        productOptionGroupService.saveOptionGroups(productId, List.of(
                new OptionGroupLinkCommand(og.getId(), true, false)
        ));

        assertThat(productOptionGroupRepository.findAllByOptionGroupId(og.getId())).hasSize(1);
    }

    @Test
    void 옵션그룹_연결시_존재하지_않는_옵션그룹이면_OPTION_GROUP_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> productOptionGroupService.saveOptionGroups(
                ProductFixture.Defaults.id,
                List.of(new OptionGroupLinkCommand(OptionGroupId.of("00000000-0000-0000-0000-000000000999"), true, false))
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.OPTION_GROUP_NOT_FOUND_ERROR));
    }

    @Test
    void 상품의_옵션그룹을_교체한다() {
        ProductId productId = ProductFixture.Defaults.id;
        OptionGroup old = savedOptionGroup(10L);
        OptionGroup newOg = savedOptionGroup(20L);
        productOptionGroupRepository.put(ProductOptionGroup.of(productId, old.getId(), true, false, Instant.now()));

        productOptionGroupService.replaceOptionGroups(productId, List.of(
                new OptionGroupLinkCommand(newOg.getId(), false, true)
        ));

        assertThat(productOptionGroupRepository.findAllByOptionGroupId(old.getId())).isEmpty();
        assertThat(productOptionGroupRepository.findAllByOptionGroupId(newOg.getId())).hasSize(1);
    }

    @Test
    void 상품에_연결된_모든_옵션그룹을_삭제한다() {
        ProductId productId = ProductFixture.Defaults.id;
        OptionGroup og = savedOptionGroup(10L);
        productOptionGroupRepository.put(ProductOptionGroup.of(productId, og.getId(), true, false, Instant.now()));

        productOptionGroupService.deleteAllByProductId(productId);

        assertThat(productOptionGroupRepository.findAllByOptionGroupId(og.getId())).isEmpty();
    }

    @Test
    void 연결된_상품이_있으면_LINKED_PRODUCT_EXISTS_ERROR를_던진다() {
        OptionGroup og = savedOptionGroup(10L);
        productOptionGroupRepository.put(ProductOptionGroup.of(ProductFixture.Defaults.id, og.getId(), true, false, Instant.now()));

        assertThatThrownBy(() -> productOptionGroupService.assertNoLinkedProducts(og.getId()))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.LINKED_PRODUCT_EXISTS_ERROR));
    }

    @Test
    void 연결된_상품이_없으면_예외를_던지지_않는다() {
        OptionGroup og = savedOptionGroup(10L);

        productOptionGroupService.assertNoLinkedProducts(og.getId());
    }

    @Test
    void 옵션그룹_연결시_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        productOptionGroupRepository.throwOnNextCall();

        assertThatThrownBy(() -> productOptionGroupService.deleteAllByProductId(ProductFixture.Defaults.id))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }
}
