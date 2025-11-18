package com.springboot.auftragsmanagement.entity;

public class NullPurchaseOrderItem extends PurchaseOrderItem {

    private static final NullPurchaseOrderItem INSTANCE = new NullPurchaseOrderItem();

    private NullPurchaseOrderItem() {
        this.setId(0L);
        this.setPurchaseOrder(NullPurchaseOrder.getInstance());
        this.setArticle(NullArticle.getInstance());
        this.setQuantity(0);
        this.setUnitPrice(0.0);
    }

    public static NullPurchaseOrderItem getInstance() {
        return INSTANCE;
    }

    public boolean isNull() {
        return true;
    }
}