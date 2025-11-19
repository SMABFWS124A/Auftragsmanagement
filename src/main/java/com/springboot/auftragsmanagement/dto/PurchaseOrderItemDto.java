package com.springboot.auftragsmanagement.dto;

public record PurchaseOrderItemDto(
        Long articleId,
        int quantity,
        Double unitPrice
) {

public static Builder builder() {
    return new Builder();
}

public static final class Builder {
    private Long articleId;
    private int quantity;
    private Double unitPrice;

    private Builder() {
    }

    public Builder articleId(Long articleId) {
        this.articleId = articleId;
        return this;
    }

    public Builder quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public Builder unitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public PurchaseOrderItemDto build() {
        return new PurchaseOrderItemDto(articleId, quantity, unitPrice);
    }
}
}