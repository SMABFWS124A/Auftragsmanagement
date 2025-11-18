package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.PurchaseOrderItemDto;

public interface PurchaseOrderItemFactory {
    PurchaseOrderItemDto createPurchaseOrderItem(Long articleId, int quantity, Double unitPrice);
}