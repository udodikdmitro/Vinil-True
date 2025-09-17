package com.vinylshop.dto;

import java.util.List;

public record NotificationRequest(
        List<String> addressesTo,
        String subject,
        String text
) {}
