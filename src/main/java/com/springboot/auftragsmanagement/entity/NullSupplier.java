package com.springboot.auftragsmanagement.entity;

public class NullSupplier extends Supplier {

    private static final NullSupplier INSTANCE = new NullSupplier();

    private NullSupplier() {
        this.setId(0L);
        this.setName("Unbekannter Lieferant");
        this.setEmail("");
        this.setPhone("");
        this.setAddress("");
        this.setContactPerson("");
    }

    public static NullSupplier getInstance() {
        return INSTANCE;
    }

    public boolean isNull() {
        return true;
    }
}