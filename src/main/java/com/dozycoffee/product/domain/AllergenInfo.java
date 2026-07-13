package com.dozycoffee.product.domain;

import java.util.*;
import java.util.stream.Collectors;

public class AllergenInfo {

    private final Set<AllergenType> allergens;

    public AllergenInfo(Set<AllergenType> allergens) {
        this.allergens = new HashSet<>(allergens);
    }

    public static AllergenInfo of(String allergensStr) {
        if (allergensStr == null || allergensStr.isEmpty()) return new AllergenInfo(Collections.emptySet());
        Set<AllergenType> allergens = Arrays.stream(allergensStr.split(","))
                .map(String::strip)
                .map(AllergenType::of)
                .collect(Collectors.toSet());
        return new AllergenInfo(allergens);
    }

    @Override
    public String toString() {
        return allergens.stream()
                .sorted(Comparator.comparing(Enum::name))
                .map(t -> t.name().toLowerCase())
                .collect(Collectors.joining(","));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AllergenInfo other)) return false;
        return allergens.equals(other.allergens);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allergens);
    }

}
