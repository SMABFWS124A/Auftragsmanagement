package com.springboot.auftragsmanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderDto(
        Long id,
        Long supplierId,
        String supplierName,
        LocalDateTime orderDate,
        String status,
        Double totalAmount,
        List<PurchaseOrderItemDto> items
) {}