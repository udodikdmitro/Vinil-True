package com.vinylshop.util;

import java.util.Currency;
import java.util.Locale;

public final class Constants {
    private Constants() {}

    public static final Locale DEFAULT_CURRENCY_LOCALE = Locale.forLanguageTag("uk-UA");
    public static final Currency DEFAULT_CURRENCY = Currency.getInstance(DEFAULT_CURRENCY_LOCALE);

}
