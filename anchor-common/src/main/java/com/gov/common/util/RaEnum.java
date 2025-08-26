package com.gov.common.util;

public enum RaEnum {
    A(1),
    B(2),
    C(3),
    D(4);

    private final int value;

    RaEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // 根据值获取枚举项
    public static RaEnum fromValue(int value) {
        for (RaEnum e : RaEnum.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
    public static boolean contains(String name) {
        for (RaEnum e : RaEnum.values()) {
            if (e.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static int getCode(String name) {
        for (RaEnum e : RaEnum.values()) {
            if (e.name().equalsIgnoreCase(name)) {
                return e.getValue();
            }
        }
        return 0;
    }
}
