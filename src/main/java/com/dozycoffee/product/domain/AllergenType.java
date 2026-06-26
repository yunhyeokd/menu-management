package com.dozycoffee.product.domain;

public enum AllergenType {

    // 곡류
    WHEAT,
    BUCKWHEAT,
    RYE,
    OAT,

    // 두류 및 견과류
    PEANUT,
    SOYBEAN,
    WALNUT,
    PINE_NUT,
    CASHEW,
    ALMOND,
    MACADAMIA,
    PISTACHIO,
    HAZELNUT,

    // 동물성
    MILK,
    EGG,
    BEEF,
    PORK,
    CHICKEN,
    SHRIMP,
    CRAB,
    SQUID,
    CLAM;

    public static AllergenType of(String code) {
        for (AllergenType allergenType : AllergenType.values()) {
            if (allergenType.name().equalsIgnoreCase(code)) {
                return allergenType;
            }
        }
        throw new ProductException("Unknown allergen code: " + code);
    }
}