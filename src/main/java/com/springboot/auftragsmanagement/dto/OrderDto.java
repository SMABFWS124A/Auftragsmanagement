package com.springboot.auftragsmanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        Long customerId,
        LocalDateTime orderDate,
        String status,
        Double totalAmount,
        List<OrderItemDto> items
) {

public static Builder builder() {
    return new Builder();
}

public static final class Builder {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private String status;
    private Double totalAmount;
    private List<OrderItemDto> items;

    private Builder() {
    }

    public Builder id(Long id) {
        this.id = id;
        return this;
    }

    public Builder customerId(Long customerId) {
        this.customerId = customerId;
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

    public Builder items(List<OrderItemDto> items) {
        this.items = items;
        return this;
    }

    public OrderDto build() {
        return new OrderDto(id, customerId, orderDate, status, totalAmount, items);
    }
}
}