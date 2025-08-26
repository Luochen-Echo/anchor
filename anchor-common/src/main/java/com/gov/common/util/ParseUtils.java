package com.gov.common.util;

public class ParseUtils {
    public static double parseSafeDouble(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        return Double.parseDouble(value.replaceAll("[^\\d.-]", ""));
    }

    public static int parseSafeInt(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        return Integer.parseInt(value.replaceAll("[^\\d-]", ""));
    }
}
