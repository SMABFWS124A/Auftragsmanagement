package com.springboot.auftragsmanagement.entity;

public class NullOrderItem extends OrderItem {

    private static final NullOrderItem INSTANCE = new NullOrderItem();

    private NullOrderItem() {
        this.setId(0L);
        this.setArticle(NullArticle.getInstance());
        this.setQuantity(0);
        this.setUnitPrice(0.0);
    }

    public static NullOrderItem getInstance() {
        return INSTANCE;
    }

    public boolean isNull() {
        return true;
    }
}