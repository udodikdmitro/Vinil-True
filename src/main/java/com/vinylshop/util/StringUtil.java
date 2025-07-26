package com.vinylshop.util;

public final class StringUtil {
    private StringUtil() {}

    public static String removeBrackets(String s) {
        if (s == null) return null;
        int start = s.indexOf('(');
        return start == -1 ? s.trim() : s.substring(0, start).trim();
    }

}
