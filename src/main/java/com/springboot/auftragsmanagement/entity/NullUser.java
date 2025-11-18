package com.springboot.auftragsmanagement.entity;

public class NullUser extends User {

    private static final NullUser INSTANCE = new NullUser();

    private NullUser() {
        this.setId(0L);
        this.setFirstName("Unbekannt");
        this.setLastName("Benutzer");
        this.setEmail("unknown@example.com");
    }

    public static NullUser getInstance() {
        return INSTANCE;
    }

    public boolean isNull() {
        return true;
    }
}