package com.vinylshop.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyRateService.class);

    private static final Map<String, BigDecimal> testRates;
    static {
        testRates = new ConcurrentHashMap<>();
        final BigDecimal one = BigDecimal.ONE.setScale(2, RoundingMode.HALF_EVEN);
        testRates.put("UAHUAH", one);
        testRates.put("USDUSD", one);
        testRates.put("USDUAH", new BigDecimal("41.729241"));
        testRates.put("UAHUSD", new BigDecimal("0.023964"));
    }

    public BigDecimal getRate(Currency source, Currency target) {
        Objects.requireNonNull(source, "source is null");
        Objects.requireNonNull(target, "target is null");

        if (source.equals(target)) {
            return BigDecimal.ONE.setScale(2, RoundingMode.HALF_EVEN);
        }

        // TODO: Replace fetching rate from map to fetching from api in the future
        return getRateFromMap(source, target);
    }

    private BigDecimal getRateFromMap(Currency source, Currency target) {
        final String key = source.getCurrencyCode() + target.getCurrencyCode();
        return testRates.computeIfAbsent(key, k -> BigDecimal.ONE.setScale(2, RoundingMode.HALF_EVEN));
    }

}