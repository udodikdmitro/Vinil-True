package com.vinylshop.util;

import java.util.Currency;
import java.util.Locale;

public final class CurrencyUtil {
    private CurrencyUtil() {}

    public static Currency getCurrencyFromLocaleOrDefault(Locale locale, Currency defaultCurrency) {
        try {
            return locale != null ? Currency.getInstance(locale) : defaultCurrency;
        } catch (IllegalArgumentException ex) {
            return defaultCurrency;
        }
    }

}
