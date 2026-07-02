package com.dozycoffee.infrastructure.web;

import com.dozycoffee.branch.domain.BranchId;
import org.junit.jupiter.api.Test;
import org.springframework.format.support.DefaultFormattingConversionService;

import static org.assertj.core.api.Assertions.assertThat;

class IdConverterFactoryTest {

    @Test
    void 문자열을_ID_VO로_변환한다() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(new IdConverterFactory<BranchId>(BranchId::of) {});

        BranchId branchId = conversionService.convert("branch-raw-id", BranchId.class);

        assertThat(branchId).isEqualTo(BranchId.of("branch-raw-id"));
    }
}
