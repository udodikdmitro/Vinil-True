package com.vinylshop.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(3))
            .setReadTimeout(Duration.ofSeconds(7))
            .build();
    }

    public ConverterFactory<String, Enum<?>> stringToEnumIgnoreCaseConverterFactory() {
        return new ConverterFactory<>() {
            @Override
            public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
                return source -> {
                    if (!source.isBlank()) {
                        String value = source.trim();
                        for (T constant : targetType.getEnumConstants()) {
                            if (constant.name().equalsIgnoreCase(value)) {
                                return constant;
                            }
                        }
                    }
                    throw new ConversionFailedException(
                        TypeDescriptor.valueOf(String.class),
                        TypeDescriptor.valueOf(targetType),
                        source,
                        new IllegalArgumentException("Unknown enum value: " + source)
                    );                };
            }
        };
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(stringToEnumIgnoreCaseConverterFactory());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowedOriginPatterns("*")
            .allowCredentials(true);
    }

}
