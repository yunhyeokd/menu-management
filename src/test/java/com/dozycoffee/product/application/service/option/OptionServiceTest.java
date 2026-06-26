package com.dozycoffee.product.application.service.option;

import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.ServiceError;
import com.dozycoffee.core.application.exception.*;
import com.dozycoffee.product.application.repository.FakeOptionGroupRepository;
import com.dozycoffee.product.application.repository.FakeOptionItemRepository;
import com.dozycoffee.product.application.dto.*;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OptionServiceTest {

    private FakeOptionGroupRepository optionGroupRepository;
    private FakeOptionItemRepository optionItemRepository;
    private OptionService optionService;
    private long nextOptionGroupId = 1L;
    private long nextOptionItemId = 1L;

    @BeforeEach
    public void setUp() {
        optionGroupRepository = new FakeOptionGroupRepository();
        optionItemRepository = new FakeOptionItemRepository();
        nextOptionGroupId = 1L;
        nextOptionItemId = 1L;
        optionService = new OptionService(
                optionGroupRepository,
                optionItemRepository,
                () -> OptionGroupId.of(nextOptionGroupId++),
                () -> OptionItemId.of(nextOptionItemId++)
        );
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    private OptionGroupCreateCommand createCommand(String name, List<OptionItemCreateCommand> items) {
        return new OptionGroupCreateCommand(name, Optional.empty(), items);
    }

    private OptionItemCreateCommand itemCommand(String name, int price) {
        return new OptionItemCreateCommand(name, Optional.empty(), price);
    }

    // ─── create ──────────────────────────────────────────────────────────────

    @Test
    public void 옵션_그룹을_정상_생성한다() {
        OptionGroupCreateCommand command = createCommand("사이즈", List.of(itemCommand("S", 0), itemCommand("L", 500)));

        OptionGroupData result = optionService.create(command);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("사이즈");
        assertThat(result.items()).hasSize(2);
        assertThat(optionGroupRepository.contains(result.id())).isTrue();
    }

    @Test
    public void 옵션_그룹_생성시_아이템이_없으면_EMPTY_OPTION_GROUP_ERROR를_던진다() {
        OptionGroupCreateCommand command = createCommand("사이즈", List.of());

        assertThatThrownBy(() -> optionService.create(command))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.EMPTY_OPTION_GROUP_ERROR));
    }

    @Test
    public void 옵션_그룹_생성시_이름이_유효하지_않으면_INVALID_OPTION_ERROR를_던진다() {
        OptionGroupCreateCommand command = createCommand("", List.of(itemCommand("S", 0)));

        assertThatThrownBy(() -> optionService.create(command))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.INVALID_OPTION_ERROR));
    }

    @Test
    public void 옵션_그룹_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        optionGroupRepository.throwOnNextCall();

        assertThatThrownBy(() -> optionService.create(createCommand("사이즈", List.of(itemCommand("S", 0)))))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── findAll ─────────────────────────────────────────────────────────────

    @Test
    public void 옵션_그룹_전체_목록을_조회한다() {
        OptionGroup group1 = OptionGroup.of(OptionGroupId.of(1L), "사이즈", null, Instant.now());
        OptionGroup group2 = OptionGroup.of(OptionGroupId.of(2L), "온도", null, Instant.now());
        optionGroupRepository.put(group1);
        optionGroupRepository.put(group2);
        optionItemRepository.put(OptionItem.of(OptionItemId.of(1L), group1.getId(), "S", null, 0, Instant.now()));
        optionItemRepository.put(OptionItem.of(OptionItemId.of(2L), group2.getId(), "Hot", null, 0, Instant.now()));

        List<OptionGroupData> result = optionService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).items()).hasSize(1);
        assertThat(result.get(1).items()).hasSize(1);
    }

    @Test
    public void 옵션_그룹이_없으면_빈_목록을_반환한다() {
        assertThat(optionService.findAll()).isEmpty();
    }

    @Test
    public void 옵션_그룹_전체_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        optionGroupRepository.throwOnNextCall();

        assertThatThrownBy(() -> optionService.findAll())
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    // ─── updateOptionGroupProfile ─────────────────────────────────────────────

    @Test
    public void 옵션_그룹_프로필을_정상_수정한다() {
        optionGroupRepository.put(OptionGroup.of(OptionGroupId.of(1L), "사이즈", null, Instant.now()));
        OptionGroupProfileUpdateCommand command = new OptionGroupProfileUpdateCommand(
                OptionGroupId.of(1L), "Size", Optional.of("음료 사이즈")
        );

        optionService.updateOptionGroupProfile(command);

        OptionGroup updated = optionGroupRepository.findById(OptionGroupId.of(1L)).get();
        assertThat(updated.getName()).isEqualTo("Size");
        assertThat(updated.getDescription()).isEqualTo("음료 사이즈");
    }

    @Test
    public void 옵션_그룹_프로필_수정시_대상이_존재하지_않으면_OPTION_GROUP_NOT_FOUND_ERROR를_던진다() {
        OptionGroupProfileUpdateCommand command = new OptionGroupProfileUpdateCommand(
                OptionGroupId.of(999L), "Size", Optional.empty()
        );

        assertThatThrownBy(() -> optionService.updateOptionGroupProfile(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.OPTION_GROUP_NOT_FOUND_ERROR));
    }

    @Test
    public void 옵션_그룹_프로필_수정시_이름이_유효하지_않으면_INVALID_OPTION_ERROR를_던진다() {
        optionGroupRepository.put(OptionGroup.of(OptionGroupId.of(1L), "사이즈", null, Instant.now()));
        OptionGroupProfileUpdateCommand command = new OptionGroupProfileUpdateCommand(
                OptionGroupId.of(1L), "", Optional.empty()
        );

        assertThatThrownBy(() -> optionService.updateOptionGroupProfile(command))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.INVALID_OPTION_ERROR));
    }

    // ─── updateOptionGroupItems ───────────────────────────────────────────────

    @Test
    public void 옵션_그룹_아이템을_정상_수정한다() {
        optionGroupRepository.put(OptionGroup.of(OptionGroupId.of(1L), "사이즈", null, Instant.now()));
        optionItemRepository.put(OptionItem.of(OptionItemId.of(1L), OptionGroupId.of(1L), "S", null, 0, Instant.now()));
        OptionGroupItemUpdateCommand command = new OptionGroupItemUpdateCommand(
                OptionGroupId.of(1L),
                List.of(itemCommand("M", 300), itemCommand("L", 500))
        );

        optionService.updateOptionGroupItems(command);

        List<OptionItem> items = optionItemRepository.findAllByOptionGroupId(OptionGroupId.of(1L));
        assertThat(items).hasSize(2);
        assertThat(items).extracting(OptionItem::getName).containsExactlyInAnyOrder("M", "L");
    }

    @Test
    public void 옵션_그룹_아이템_수정시_아이템이_없으면_EMPTY_OPTION_GROUP_ERROR를_던진다() {
        optionGroupRepository.put(OptionGroup.of(OptionGroupId.of(1L), "사이즈", null, Instant.now()));
        OptionGroupItemUpdateCommand command = new OptionGroupItemUpdateCommand(OptionGroupId.of(1L), List.of());

        assertThatThrownBy(() -> optionService.updateOptionGroupItems(command))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.EMPTY_OPTION_GROUP_ERROR));
    }

    @Test
    public void 옵션_그룹_아이템_수정시_대상_그룹이_존재하지_않으면_OPTION_GROUP_NOT_FOUND_ERROR를_던진다() {
        OptionGroupItemUpdateCommand command = new OptionGroupItemUpdateCommand(
                OptionGroupId.of(999L), List.of(itemCommand("S", 0))
        );

        assertThatThrownBy(() -> optionService.updateOptionGroupItems(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.OPTION_GROUP_NOT_FOUND_ERROR));
    }

    // ─── deleteOptionGroup ────────────────────────────────────────────────────

    @Test
    public void 옵션_그룹을_정상_삭제한다() {
        optionGroupRepository.put(OptionGroup.of(OptionGroupId.of(1L), "사이즈", null, Instant.now()));
        optionItemRepository.put(OptionItem.of(OptionItemId.of(1L), OptionGroupId.of(1L), "S", null, 0, Instant.now()));

        optionService.deleteOptionGroup(OptionGroupId.of(1L));

        assertThat(optionGroupRepository.contains(OptionGroupId.of(1L))).isFalse();
        assertThat(optionItemRepository.findAllByOptionGroupId(OptionGroupId.of(1L))).isEmpty();
    }

    @Test
    public void 옵션_그룹_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        optionGroupRepository.put(OptionGroup.of(OptionGroupId.of(1L), "사이즈", null, Instant.now()));
        optionItemRepository.throwOnNextCall();

        assertThatThrownBy(() -> optionService.deleteOptionGroup(OptionGroupId.of(1L)))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }
}
