package com.vinylshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {

    private final CurrencyRateService rateService;

    public BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency) {
        Objects.requireNonNull(amount, "amount is null");
        Objects.requireNonNull(sourceCurrency, "sourceCurrency is null");
        Objects.requireNonNull(targetCurrency, "targetCurrency is null");
        if (Objects.equals(sourceCurrency, targetCurrency)) {
            return amount.setScale(2, RoundingMode.HALF_EVEN);
        }
        final BigDecimal rate = rateService.getRate(sourceCurrency, targetCurrency);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
    }

}