package com.springboot.auftragsmanagement.dto;

public class SupplierDto {

    private Long id;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;

    public SupplierDto() {
    }

    private SupplierDto(Long id, String name, String contactPerson, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

public static final class Builder {
    private Long id;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;

    private Builder() {
    }

    public Builder id(Long id) {
        this.id = id;
        return this;
    }

    public Builder name(String name) {
        this.name = name;
        return this;
    }

    public Builder contactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
        return this;
    }

    public Builder email(String email) {
        this.email = email;
        return this;
    }

    public Builder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public Builder address(String address) {
        this.address = address;
        return this;
    }

    public SupplierDto build() {
        return new SupplierDto(id, name, contactPerson, email, phone, address);
    }
}
}