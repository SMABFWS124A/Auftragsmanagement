package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.PurchaseOrderDto;
import com.springboot.auftragsmanagement.dto.PurchaseOrderItemDto;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseOrderFactory {
    PurchaseOrderDto createPurchaseOrder(
            Long supplierId,
            String supplierName, // <-- NEU
            LocalDateTime orderDate, // <-- NEU
            Long orderId,
            String status,
            double totalAmount,
            List<PurchaseOrderItemDto> items
    );
}