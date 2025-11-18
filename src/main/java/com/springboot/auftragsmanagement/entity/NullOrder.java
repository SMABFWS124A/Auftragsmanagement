package com.springboot.auftragsmanagement.entity;

import java.time.LocalDateTime;
import java.util.Collections;

public class NullOrder extends Order {

    private static final NullOrder INSTANCE = new NullOrder();

    private NullOrder() {
        this.setId(0L);
        this.setCustomer(NullUser.getInstance());
        this.setOrderDate(LocalDateTime.MIN);
        this.setStatus("Keine Bestellung");
        this.setTotalAmount(0.0);
        this.setItems(Collections.emptyList());
    }

    public static NullOrder getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isNull() {
        return true;
    }
}