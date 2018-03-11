package com.otaconisme.maydeena.enums;

/**
 * Created by Zakwan on 11/14/2017.
 * Enumeration for Task's weight
 */
public enum TaskWeightEnum {

    MIN(1),
    MAX(9),
    DEFAULT(5);

    private final int value;

    TaskWeightEnum(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
