package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.PurchaseOrderItemDto;
import org.springframework.stereotype.Component;

@Component
public class DefaultPurchaseOrderItemFactory implements PurchaseOrderItemFactory {

    @Override
    public PurchaseOrderItemDto createPurchaseOrderItem(Long articleId, int quantity, Double unitPrice) {
        return PurchaseOrderItemDto.builder()
                .articleId(articleId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .build();
    }
}