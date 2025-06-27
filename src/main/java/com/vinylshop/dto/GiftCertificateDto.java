package com.vinylshop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GiftCertificateDto extends ProductDto {

    private String code;
    private BigDecimal value;
    private Boolean isUsed;
    private UserDto issuedTo;
    private LocalDateTime expiresAt;

}
