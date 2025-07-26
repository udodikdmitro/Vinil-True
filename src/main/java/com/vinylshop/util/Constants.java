package com.vinylshop.util;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

public final class Constants {
    private Constants() {}

    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("uk-UA");
    public static final Currency DEFAULT_CURRENCY = Currency.getInstance(DEFAULT_LOCALE);
    public static final BigDecimal BIG_DECIMAL_EMPTY_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);

}