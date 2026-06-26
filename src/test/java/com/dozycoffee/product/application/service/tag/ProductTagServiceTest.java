package com.dozycoffee.product.application.service.tag;

import com.dozycoffee.core.application.AppException;
import com.dozycoffee.core.application.ServiceError;
import com.dozycoffee.core.application.exception.SystemException;
import com.dozycoffee.product.application.dto.TagData;
import com.dozycoffee.product.application.repository.FakeProductTagRepository;
import com.dozycoffee.product.application.repository.FakeTagRepository;
import com.dozycoffee.product.application.service.ProductErrors;
import com.dozycoffee.product.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductTagServiceTest {

    private FakeProductTagRepository productTagRepository;
    private FakeTagRepository tagRepository;
    private TagService tagService;
    private ProductTagService productTagService;
    private long nextTagId = 1L;

    @BeforeEach
    void setUp() {
        productTagRepository = new FakeProductTagRepository();
        tagRepository = new FakeTagRepository();
        tagService = new TagService(tagRepository, () -> TagId.of(nextTagId++));
        productTagService = new ProductTagService(productTagRepository, tagService);
    }

    private void assertErrorCode(Throwable e, ServiceError error) {
        assertThat(((AppException) e).getErrorCode()).isEqualTo(error.getErrorCode());
    }

    @Test
    void 태그를_상품에_연결한다() {
        ProductId productId = ProductId.of(1L);
        tagRepository.put(Tag.of(TagId.of(10L), "신제품", Instant.now()));

        List<TagData> tags = productTagService.saveTags(productId, Set.of("신제품"));

        assertThat(tags).hasSize(1);
        assertThat(tags.get(0).name()).isEqualTo("신제품");
        assertThat(productTagRepository.all()).hasSize(1);
    }

    @Test
    void 태그가_없으면_자동_생성_후_연결한다() {
        ProductId productId = ProductId.of(1L);

        List<TagData> tags = productTagService.saveTags(productId, Set.of("새태그"));

        assertThat(tags).hasSize(1);
        assertThat(tags.get(0).name()).isEqualTo("새태그");
        assertThat(tagRepository.findByName("새태그")).isPresent();
        assertThat(productTagRepository.all()).hasSize(1);
    }

    @Test
    void 태그_연결시_레포지토리_오류가_발생하면_UNKNOWN_ERROR를_던진다() {
        productTagRepository.throwOnNextCall();

        assertThatThrownBy(() -> productTagService.saveTags(ProductId.of(1L), Set.of("신제품")))
                .isInstanceOf(SystemException.class)
                .satisfies(e -> assertErrorCode(e, ProductErrors.UNKNOWN_ERROR));
    }

    @Test
    void 상품의_태그를_교체한다() {
        ProductId productId = ProductId.of(1L);
        Tag oldTag = tagRepository.put(Tag.of(TagId.of(10L), "구태그", Instant.now()));
        productTagRepository.add(ProductTag.of(productId, oldTag.getId(), Instant.now()));

        List<TagData> tags = productTagService.replaceTags(productId, Set.of("신태그"));

        assertThat(tags).hasSize(1);
        assertThat(tags.get(0).name()).isEqualTo("신태그");
        assertThat(productTagRepository.findAllByTagId(oldTag.getId())).isEmpty();
    }

    @Test
    void 상품에_연결된_모든_태그를_삭제한다() {
        ProductId productId = ProductId.of(1L);
        Tag tag = tagRepository.put(Tag.of(TagId.of(10L), "신제품", Instant.now()));
        productTagRepository.add(ProductTag.of(productId, tag.getId(), Instant.now()));

        productTagService.deleteAllByProductId(productId);

        assertThat(productTagRepository.all()).isEmpty();
    }

    @Test
    void 태그에_연결된_상품_id_목록을_조회한다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(10L), "신제품", Instant.now()));
        productTagRepository.add(ProductTag.of(ProductId.of(1L), tag.getId(), Instant.now()));
        productTagRepository.add(ProductTag.of(ProductId.of(2L), tag.getId(), Instant.now()));

        List<ProductId> productIds = productTagService.findLinkedProductIds(tag.getId());

        assertThat(productIds).containsExactlyInAnyOrder(ProductId.of(1L), ProductId.of(2L));
    }

    @Test
    void 태그_id로_연결된_모든_상품_태그를_삭제한다() {
        Tag tag = tagRepository.put(Tag.of(TagId.of(10L), "신제품", Instant.now()));
        productTagRepository.add(ProductTag.of(ProductId.of(1L), tag.getId(), Instant.now()));
        productTagRepository.add(ProductTag.of(ProductId.of(2L), tag.getId(), Instant.now()));

        productTagService.deleteAllByTagId(tag.getId());

        assertThat(productTagRepository.findAllByTagId(tag.getId())).isEmpty();
    }
}
