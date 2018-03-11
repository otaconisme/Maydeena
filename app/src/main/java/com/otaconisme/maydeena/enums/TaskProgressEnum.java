package com.otaconisme.maydeena.enums;

/**
 * Created by Zakwan on 11/15/2017.
 * Enumeration for Task's progress
 */

public enum TaskProgressEnum {

    UNDONE(0),
    DONE(1);

    private final double value;

    TaskProgressEnum(final double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
