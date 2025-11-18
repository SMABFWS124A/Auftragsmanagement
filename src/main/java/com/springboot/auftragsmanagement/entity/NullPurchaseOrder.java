package com.springboot.auftragsmanagement.entity;

import java.time.LocalDateTime;
import java.util.Collections;

public class NullPurchaseOrder extends PurchaseOrder {

    private static final NullPurchaseOrder INSTANCE = new NullPurchaseOrder();

    private NullPurchaseOrder() {
        this.setId(0L);
        this.setSupplier(NullSupplier.getInstance());
        this.setOrderDate(LocalDateTime.MIN);
        this.setStatus("Keine Bestellung");
        this.setTotalAmount(0.0);
        this.setItems(Collections.emptyList());
    }

    public static NullPurchaseOrder getInstance() {
        return INSTANCE;
    }

    public boolean isNull() {
        return true;
    }
}