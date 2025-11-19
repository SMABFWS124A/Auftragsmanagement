package com.springboot.auftragsmanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderDto(
        Long id,
        Long supplierId,
        String supplierName,
        LocalDateTime orderDate,
        String status,
        Double totalAmount,
        List<PurchaseOrderItemDto> items
) {

public static Builder builder() {
    return new Builder();
}

public static final class Builder {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private LocalDateTime orderDate;
    private String status;
    private Double totalAmount;
    private List<PurchaseOrderItemDto> items;

    private Builder() {
    }

    public Builder id(Long id) {
        this.id = id;
        return this;
    }

    public Builder supplierId(Long supplierId) {
        this.supplierId = supplierId;
        return this;
    }

    public Builder supplierName(String supplierName) {
        this.supplierName = supplierName;
        return this;
    }

    public Builder orderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public Builder status(String status) {
        this.status = status;
        return this;
    }

    public Builder totalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public Builder items(List<PurchaseOrderItemDto> items) {
        this.items = items;
        return this;
    }

    public PurchaseOrderDto build() {
        return new PurchaseOrderDto(id, supplierId, supplierName, orderDate, status, totalAmount, items);
    }
}
}