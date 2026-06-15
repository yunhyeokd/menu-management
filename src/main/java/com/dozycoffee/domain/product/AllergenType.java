package com.dozycoffee.domain.product;

public enum AllergenType {

    // 곡류
    WHEAT("밀"),
    BUCKWHEAT("메밀"),
    RYE("호밀"),
    OAT("귀리"),

    // 두류 및 견과류
    PEANUT("땅콩"),
    SOYBEAN("대두"),
    WALNUT("호두"),
    PINE_NUT("잣"),
    CASHEW("캐슈넛"),
    ALMOND("아몬드"),
    MACADAMIA("마카다미아"),
    PISTACHIO("피스타치오"),
    HAZELNUT("헤이즐넛"),

    // 동물성
    MILK("우유"),
    EGG("난류"),
    BEEF("쇠고기"),
    PORK("돼지고기"),
    CHICKEN("닭고기"),
    SHRIMP("새우"),
    CRAB("게"),
    SQUID("오징어"),
    CLAM("조개류");

    private final String value;

    AllergenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AllergenType of(String code) {
        for (AllergenType allergenType : AllergenType.values()) {
            if (allergenType.name().equalsIgnoreCase(code)) {
                return allergenType;
            }
        }
        throw new ProductException("Unknown allergen code: " + code);
    }
}