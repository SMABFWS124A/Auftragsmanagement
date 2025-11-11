package com.springboot.auftragsmanagement.dto;

public record OrderItemDto(
        Long articleId,
        int quantity,
        double unitPrice
) {}