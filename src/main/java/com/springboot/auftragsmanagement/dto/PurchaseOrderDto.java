package com.springboot.auftragsmanagement.dto;

import java.util.List;

public record PurchaseOrderDto(
        Long id,
        Long supplierId,
        String status,
        Double totalAmount,
        List<PurchaseOrderItemDto> items
) {}