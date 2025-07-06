package com.vinylshop.util;

import com.vinylshop.entity.GoldmineCondition;

import static com.vinylshop.entity.GoldmineCondition.*;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public final class GoldmineUtil {
    private GoldmineUtil() {}

    public static final Set<GoldmineCondition> ALL_CONDITIONS = Collections
        .unmodifiableSet(EnumSet.allOf(GoldmineCondition.class));

    public static final Set<GoldmineCondition> RECORD_CONDITIONS = ALL_CONDITIONS;

    public static final Set<GoldmineCondition> ENVELOP_CONDITIONS = Collections
        .unmodifiableSet(EnumSet.of(
            SS, M, NM, VG, VG_MINUS, VG_PLUS, G, F, EX, EX_MINUS, EX_PLUS
        ));

    public static GoldmineCondition recordConditionFromCode(String code) {
        return fromCode(RECORD_CONDITIONS, code);
    }

    public static GoldmineCondition envelopConditionFromCode(String code) {
        return fromCode(ENVELOP_CONDITIONS, code);
    }

    public static GoldmineCondition fromCode(String code) {
        return fromCode(ALL_CONDITIONS, code);
    }

    public static GoldmineCondition fromCode(Set<GoldmineCondition> conditions, String code) {
        final String trimCode = normalizeCode(code);
        if (trimCode == null) return null;

        final String left, right, combined;

        if (trimCode.contains("/")) {
            String[] parts = trimCode.split("/", 2);
            left = parts[0].trim();
            right = parts.length > 1 ? parts[1].trim() : "";
            combined = left + right;
        } else {
            left = trimCode;
            right = "";
            combined = null;
        }

        for (GoldmineCondition condition : conditions) {
            if (condition.code().equalsIgnoreCase(left) ||
                (condition.code().equalsIgnoreCase(right)) ||
                (condition.code().equalsIgnoreCase(combined))) {
                return condition;
            }
        }

        return null;
    }

    public static String normalizeCode(String code) {
        if (code != null && !code.isBlank()) {
            final String[] parts = code.trim().split("\\s+");
            for (String part : parts) {
                if (indexStartBracket(part) == -1) {
                    return part;
                }
            }
        }
        return null;
    }

    private static int indexStartBracket(String str) {
        final char[] brackets = {'(', '[', '{'};
        for(char b : brackets) {
            final int bracket = str.indexOf(b);
            if (bracket != -1) {
                return bracket;
            }
        }
        return -1;
    }


}
