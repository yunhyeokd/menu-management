package com.dozycoffee.product.application.service.tag;

import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.ServiceError;
import com.dozycoffee.core.application.exception.*;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.repository.FakeTagRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.domain.Tag;
import com.dozycoffee.product.domain.TagId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TagServiceTest {

    private FakeTagRepository tagRepository;
    private TagService tagService;
    private long nextTagId = 1L;

    @BeforeEach
    public void setUp() {
        tagRepository = new FakeTagRepository();
        nextTagId = 1L;
        tagService = new TagService(tagRepository, () -> TagId.of(String.format("00000000-0000-0000-0000-%012d", nextTagId++)));
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    @Test
    public void 태그를_정상_생성한다() {
        String tagName = "신제품";

        TagData createdTag = tagService.create(tagName);

        assertThat(createdTag.id()).isNotNull();
        assertThat(createdTag.name()).isEqualTo(tagName);
        assertThat(tagRepository.findById(createdTag.id())).isPresent();
    }

    @Test
    public void 태그_생성시_이름이_중복되면_DUPLICATE_TAG_NAME_ERROR를_던진다() {
        String tagName = "신제품";
        tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), tagName, Instant.now()));

        assertThatThrownBy(() -> tagService.create(tagName))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.DUPLICATE_TAG_NAME_ERROR));
    }

    @Test
    public void 태그_생성시_이름이_유효하지_않으면_INVALID_TAG_ERROR를_던진다() {
        String invalidName = "";

        assertThatThrownBy(() -> tagService.create(invalidName))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.INVALID_TAG_ERROR));
    }

    @Test
    public void 태그_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        String tagName = "신제품";
        tagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.create(tagName))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    @Test
    public void 태그_이름을_정상_변경한다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), "신제품", Instant.now()));
        String newTagName = "추천";

        tagService.changeTagName(tag.getId(), newTagName);

        assertThat(tagRepository.findById(tag.getId()).get().getName()).isEqualTo(newTagName);
    }

    @Test
    public void 태그_이름_변경시_새_이름이_중복되면_DUPLICATE_TAG_NAME_ERROR를_던진다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), "신제품", Instant.now()));
        String duplicatedName = "추천";
        tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000002"), duplicatedName, Instant.now()));

        assertThatThrownBy(() -> tagService.changeTagName(tag.getId(), duplicatedName))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.DUPLICATE_TAG_NAME_ERROR));
    }

    @Test
    public void 태그_이름_변경시_대상_태그가_존재하지_않으면_TAG_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> tagService.changeTagName(TagId.of("00000000-0000-0000-0000-000000000999"), "추천"))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.TAG_NOT_FOUND_ERROR));
    }

    @Test
    public void 태그_이름_변경시_새_이름이_유효하지_않으면_INVALID_TAG_ERROR를_던진다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), "신제품", Instant.now()));
        String invalidName = "";

        assertThatThrownBy(() -> tagService.changeTagName(tag.getId(), invalidName))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.INVALID_TAG_ERROR));
    }

    @Test
    public void 태그_이름_변경중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), "신제품", Instant.now()));
        tagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.changeTagName(tag.getId(), "추천"))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    @Test
    public void 태그_전체_목록을_조회한다() {
        tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), "신제품", Instant.now()));
        tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000002"), "추천", Instant.now()));

        List<TagData> tags = tagService.findAll();

        assertThat(tags).hasSize(2);
    }

    @Test
    public void 태그가_없으면_빈_목록을_반환한다() {
        List<TagData> tags = tagService.findAll();

        assertThat(tags).isEmpty();
    }

    @Test
    public void 태그_전체_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        tagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.findAll())
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    @Test
    public void 이름으로_태그를_검색한다() {
        tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), "신제품", Instant.now()));
        tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000002"), "추천", Instant.now()));

        List<TagData> tags = tagService.searchByName("신제");

        assertThat(tags).extracting(TagData::name).containsExactly("신제품");
    }

    @Test
    public void 이름으로_검색시_일치하는_태그가_없으면_빈_목록을_반환한다() {
        tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), "신제품", Instant.now()));

        List<TagData> tags = tagService.searchByName("존재하지않음");

        assertThat(tags).isEmpty();
    }

    @Test
    public void 이름으로_태그_검색중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        tagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.searchByName("신제품"))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    @Test
    public void 태그를_정상_삭제한다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), "신제품", Instant.now()));

        tagService.remove(tag.getId());

        assertThat(tagRepository.contains(tag.getId())).isFalse();
    }

    @Test
    public void 태그_삭제시_대상_태그가_존재하지_않으면_TAG_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> tagService.remove(TagId.of("00000000-0000-0000-0000-000000000999")))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.TAG_NOT_FOUND_ERROR));
    }

    @Test
    public void 태그_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of("00000000-0000-0000-0000-000000000001"), "신제품", Instant.now()));
        tagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.remove(tag.getId()))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }
}
