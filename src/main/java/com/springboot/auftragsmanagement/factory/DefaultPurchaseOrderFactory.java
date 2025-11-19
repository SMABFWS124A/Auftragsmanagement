package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.PurchaseOrderDto;
import com.springboot.auftragsmanagement.dto.PurchaseOrderItemDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DefaultPurchaseOrderFactory implements PurchaseOrderFactory {

    @Override
    public PurchaseOrderDto createPurchaseOrder(
            Long supplierId,
            String supplierName, // <-- NEU
            LocalDateTime orderDate, // <-- NEU
            Long orderId,
            String status,
            double totalAmount,
            List<PurchaseOrderItemDto> items
    ) {
        return PurchaseOrderDto.builder()
                .id(orderId)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .orderDate(orderDate)
                .status(status)
                .totalAmount(totalAmount)
                .items(items)
                .build();
    }
}