package com.springboot.auftragsmanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderDto(
        Long orderId,
        Long supplierId,
        String supplierName, // <-- gehört zur Lieferantenbestellung
        LocalDateTime orderDate, // <-- gehört zur Lieferantenbestellung
        String status,
        double totalAmount,
        List<PurchaseOrderItemDto> items
) {}