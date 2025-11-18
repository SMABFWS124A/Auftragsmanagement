package com.springboot.auftragsmanagement.dto;

public record PurchaseOrderItemDto(
        Long articleId,
        int quantity,
        Double unitPrice
) {}