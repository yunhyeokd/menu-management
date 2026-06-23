package com.dozycoffee.application.product.service.tag;

import com.dozycoffee.application.product.dto.TagData;
import com.dozycoffee.application.product.repository.FakeProductTagRepository;
import com.dozycoffee.application.product.repository.FakeTagRepository;
import com.dozycoffee.application.product.service.ProductBusinessException;
import com.dozycoffee.application.product.service.ProductErrors;
import com.dozycoffee.domain.product.ProductId;
import com.dozycoffee.domain.product.ProductTag;
import com.dozycoffee.domain.product.Tag;
import com.dozycoffee.domain.product.TagId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TagServiceTest {

    private FakeTagRepository tagRepository;
    private FakeProductTagRepository productTagRepository;
    private TagService tagService;
    private long nextTagId = 1L;

    @BeforeEach
    public void setUp() {
        tagRepository = new FakeTagRepository();
        productTagRepository = new FakeProductTagRepository();
        nextTagId = 1L;
        tagService = new TagService(tagRepository, productTagRepository,
                () -> TagId.of(nextTagId++));
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
        tagRepository.put(Tag.of(TagId.of(1L), tagName, Instant.now()));

        assertThatThrownBy(() -> tagService.create(tagName))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.DUPLICATE_TAG_NAME_ERROR.errorCode));
    }

    @Test
    public void 태그_생성시_이름이_유효하지_않으면_INVALID_TAG_ERROR를_던진다() {
        String invalidName = "";

        assertThatThrownBy(() -> tagService.create(invalidName))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.INVALID_TAG_ERROR.errorCode));
    }

    @Test
    public void 태그_생성중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        String tagName = "신제품";
        tagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.create(tagName))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.UNKNOWN_ERROR.errorCode));
    }

    @Test
    public void 태그_이름을_정상_변경한다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        String newTagName = "추천";

        tagService.changeTagName(tag.getId(), newTagName);

        assertThat(tagRepository.findById(tag.getId()).get().getName()).isEqualTo(newTagName);
    }

    @Test
    public void 태그_이름_변경시_새_이름이_중복되면_DUPLICATE_TAG_NAME_ERROR를_던진다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        String duplicatedName = "추천";
        tagRepository.put(Tag.of(TagId.of(2L), duplicatedName, Instant.now()));

        assertThatThrownBy(() -> tagService.changeTagName(tag.getId(), duplicatedName))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.DUPLICATE_TAG_NAME_ERROR.errorCode));
    }

    @Test
    public void 태그_이름_변경시_대상_태그가_존재하지_않으면_TAG_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> tagService.changeTagName(TagId.of(999L), "추천"))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.TAG_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 태그_이름_변경시_새_이름이_유효하지_않으면_INVALID_TAG_ERROR를_던진다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        String invalidName = "";

        assertThatThrownBy(() -> tagService.changeTagName(tag.getId(), invalidName))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.INVALID_TAG_ERROR.errorCode));
    }

    @Test
    public void 태그_이름_변경중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        tagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.changeTagName(tag.getId(), "추천"))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.UNKNOWN_ERROR.errorCode));
    }

    @Test
    public void 태그_전체_목록을_조회한다() {
        tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        tagRepository.put(Tag.of(TagId.of(2L), "추천", Instant.now()));

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
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.UNKNOWN_ERROR.errorCode));
    }

    @Test
    public void 이름으로_태그를_검색한다() {
        tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        tagRepository.put(Tag.of(TagId.of(2L), "추천", Instant.now()));

        List<TagData> tags = tagService.searchByName("신제");

        assertThat(tags).extracting(TagData::name).containsExactly("신제품");
    }

    @Test
    public void 이름으로_검색시_일치하는_태그가_없으면_빈_목록을_반환한다() {
        tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));

        List<TagData> tags = tagService.searchByName("존재하지않음");

        assertThat(tags).isEmpty();
    }

    @Test
    public void 이름으로_태그_검색중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        tagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.searchByName("신제품"))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.UNKNOWN_ERROR.errorCode));
    }

    @Test
    public void 태그에_연결된_상품_id_목록을_조회한다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        productTagRepository.add(ProductTag.of(ProductId.of(10L), tag.getId(), Instant.now()));
        productTagRepository.add(ProductTag.of(ProductId.of(20L), tag.getId(), Instant.now()));

        List<ProductId> productIds = tagService.findLinkedProductIds(tag.getId());

        assertThat(productIds).containsExactlyInAnyOrder(ProductId.of(10L), ProductId.of(20L));
    }

    @Test
    public void 연결된_상품이_없으면_빈_목록을_반환한다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));

        List<ProductId> productIds = tagService.findLinkedProductIds(tag.getId());

        assertThat(productIds).isEmpty();
    }

    @Test
    public void 연결_상품_조회시_대상_태그가_존재하지_않으면_TAG_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> tagService.findLinkedProductIds(TagId.of(999L)))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.TAG_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 연결_상품_조회중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        productTagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.findLinkedProductIds(tag.getId()))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.UNKNOWN_ERROR.errorCode));
    }

    @Test
    public void 태그를_정상_삭제한다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        productTagRepository.add(ProductTag.of(ProductId.of(10L), tag.getId(), Instant.now()));

        tagService.remove(tag.getId());

        assertThat(tagRepository.contains(tag.getId())).isFalse();
        assertThat(productTagRepository.findAllByTagId(tag.getId())).isEmpty();
    }

    @Test
    public void 태그_삭제시_대상_태그가_존재하지_않으면_TAG_NOT_FOUND_ERROR를_던진다() {
        assertThatThrownBy(() -> tagService.remove(TagId.of(999L)))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.TAG_NOT_FOUND_ERROR.errorCode));
    }

    @Test
    public void 태그_삭제중_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(1L), "신제품", Instant.now()));
        productTagRepository.throwOnNextCall();

        assertThatThrownBy(() -> tagService.remove(tag.getId()))
                .isInstanceOf(ProductBusinessException.class)
                .satisfies(e -> assertThat(((ProductBusinessException) e).getErrorCode())
                        .isEqualTo(ProductErrors.UNKNOWN_ERROR.errorCode));
    }
}
