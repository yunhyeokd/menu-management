package com.dozycoffee.catalog.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AllergenInfoTest {

    @Test
    public void 소문자_코드로_파싱한다() {
        AllergenInfo info = AllergenInfo.of("wheat,peanut,milk");
        assertThat(info.toString()).isEqualTo("milk,peanut,wheat");
    }

    @Test
    public void 대문자_코드로_파싱한다() {
        AllergenInfo info = AllergenInfo.of("WHEAT,PEANUT,MILK");
        assertThat(info.toString()).isEqualTo("milk,peanut,wheat");
    }

    @Test
    public void 코드_앞뒤_공백을_허용한다() {
        AllergenInfo info = AllergenInfo.of(" wheat , peanut , milk ");
        assertThat(info.toString()).isEqualTo("milk,peanut,wheat");
    }

    @Test
    public void toString은_소문자_알파벳순_CSV를_반환한다() {
        AllergenInfo info = AllergenInfo.of("soybean,almond,egg");
        assertThat(info.toString()).isEqualTo("almond,egg,soybean");
    }

    @Test
    public void of_toString_라운드트립이_성립한다() {
        String codes = "almond,egg,soybean";
        assertThat(AllergenInfo.of(codes).toString()).isEqualTo(codes);
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "wheat,invalid", "글루텐"})
    public void 잘못된_코드는_예외가_발생한다(String invalidCodes) {
        assertThatThrownBy(() -> AllergenInfo.of(invalidCodes))
                .isInstanceOf(ProductException.class);
    }

    @Test
    public void 순서가_달라도_같은_코드면_동등하다() {
        assertThat(AllergenInfo.of("wheat,peanut")).isEqualTo(AllergenInfo.of("peanut,wheat"));
    }

    @Test
    public void 다른_코드면_동등하지_않다() {
        assertThat(AllergenInfo.of("wheat")).isNotEqualTo(AllergenInfo.of("peanut"));
    }

}