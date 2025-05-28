package com.vinylshop.service;

import java.util.Currency;

public final class PreferredCurrencyHolder {

    private static final ThreadLocal<Currency> contextHolder = new ThreadLocal<>();

    private PreferredCurrencyHolder() {}

    public static void setCurrency(Currency currency) {
        contextHolder.set(currency);
    }

    public static Currency getCurrency() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }

}