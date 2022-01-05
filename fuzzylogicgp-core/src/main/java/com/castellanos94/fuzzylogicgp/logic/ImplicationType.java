package com.castellanos94.fuzzylogicgp.logic;

public enum ImplicationType {
    /**
     * S-Implication x -> y = d(n(x),y)
     */
    Natural,
    /**
     * QL-Implication x -> y = d(n(x),c(x,y))
     */
    Zadeh,
    /**
     * Reichenbach (S-implication) x -> y = 1 - x + x * y
     */
    Reichenbach,
    /**
     * Klir-Yuan implication (a variation of Reichenbach without a classification) x
     * -> y = 1 - x + x^2 * y
     */
    KlirYuan,
    /**
     * A-Implication x -> y = y^x
     */
    Yager;

    public static ImplicationType searchEnum(String value) {
        for (ImplicationType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
