package com.springboot.auftragsmanagement.dto;
import java.time.LocalDateTime;

public record ArticleDto(Long id,
                         String articleNumber,
                         String articleName,
                         double purchasePrice,
                         double salesPrice,
                         String category,
                         int inventory,
                         boolean active,
                         LocalDateTime creationDate,
                         String description) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String articleNumber;
        private String articleName;
        private double purchasePrice;
        private double salesPrice;
        private String category;
        private int inventory;
        private boolean active;
        private LocalDateTime creationDate;
        private String description;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder articleNumber(String articleNumber) {
            this.articleNumber = articleNumber;
            return this;
        }

        public Builder articleName(String articleName) {
            this.articleName = articleName;
            return this;
        }

        public Builder purchasePrice(double purchasePrice) {
            this.purchasePrice = purchasePrice;
            return this;
        }

        public Builder salesPrice(double salesPrice) {
            this.salesPrice = salesPrice;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder inventory(int inventory) {
            this.inventory = inventory;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public ArticleDto build() {
            return new ArticleDto(id, articleNumber, articleName, purchasePrice, salesPrice, category, inventory, active, creationDate, description);
        }
    }
}