/*package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.dto.PurchaseOrderDto;
import com.springboot.auftragsmanagement.dto.PurchaseOrderItemDto;
import com.springboot.auftragsmanagement.dto.SupplierDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


public final class DtoFactory {

    private DtoFactory() {
    }

    public static OrderDto createNewOrder(Long customerId) {
        // Erstellt OrderDto unter Verwendung des All-Args-Konstruktors (angenommen: Record)
        return new OrderDto(
                null, // id
                customerId,
                LocalDateTime.now(),
                "NEU",
                0.0, // totalAmount
                Collections.emptyList()
        );
    }


    public static OrderItemDto createEmptyOrderItem() {
        // Erstellt OrderItemDto unter Verwendung des All-Args-Konstruktors (angenommen: Record)
        return new OrderItemDto(
                null, // orderId (oder null/0, je nach Struktur)
                null, // articleId
                0, // quantity
                0.0 // unitPrice
        );
    }


    public static PurchaseOrderDto createNewPurchaseOrder(Long supplierId) {
        // Erstellt PurchaseOrderDto unter Verwendung des All-Args-Konstruktors (Record)
        return new PurchaseOrderDto(
                null, // id
                supplierId,
                LocalDateTime.now(),
                "BESTELLT",
                0.0, // totalAmount
                Collections.emptyList()
        );
    }


    public static PurchaseOrderItemDto createEmptyPurchaseOrderItem() {
        // Erstellt PurchaseOrderItemDto unter Verwendung des All-Args-Konstruktors (angenommen: Record)
        return new PurchaseOrderItemDto(
                null, // articleId
                0, // quantity
                0.0 // unitPrice
        );
    }


    public static SupplierDto createEmptySupplier() {
        // Erstellt SupplierDto unter Verwendung des All-Args-Konstruktors (angenommen: Record)
        return new SupplierDto(
                null, // id
                null, // name
                null, // contactName
                null, // contactEmail
                null  // phone
        );
    }


    public static ArticleDto createDefaultArticle() {
        // Erstellt ArticleDto unter Verwendung des All-Args-Konstruktors (Record)
        return new ArticleDto(
                null, // id
                null, // articleNumber
                null, // articleName
                0.0, // purchasePrice
                0.0, // salesPrice
                null, // category
                0, // inventory
                true, // active
                LocalDateTime.now(),
                null // description
        );
    }
}*/