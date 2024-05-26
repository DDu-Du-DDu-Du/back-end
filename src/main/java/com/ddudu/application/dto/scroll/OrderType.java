package com.ddudu.application.dto.scroll;

import java.util.Arrays;

public enum OrderType {
    LATEST;

    public static OrderType from(String value) {
        String upperValue = value.toUpperCase();

        return Arrays.stream(OrderType.values())
                .filter(type -> upperValue.equals(type.name()))
                .findFirst()
                .orElse(LATEST);
    }
}
