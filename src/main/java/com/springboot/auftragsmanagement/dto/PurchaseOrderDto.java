package com.springboot.auftragsmanagement.dto;

import java.util.List;

public record PurchaseOrderDto(
        Long id,
        Long supplierId,
        String status,
        double totalAmount,
        List<PurchaseOrderItemDto> items
) {}