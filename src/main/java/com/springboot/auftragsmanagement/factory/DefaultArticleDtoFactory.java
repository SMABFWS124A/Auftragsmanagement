package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.ArticleDto;

import java.time.LocalDateTime;

public class DefaultArticleDtoFactory implements ArticleDtoFactory {
    @Override
    public ArticleDto createArticle(Long id, String articleNumber, String articleName, double purchasePrice, double salesPrice,
                                    String category, int inventory, boolean active, LocalDateTime creationDate, String description) {
        return ArticleDto.builder()
                .id(id)
                .articleNumber(articleNumber)
                .articleName(articleName)
                .purchasePrice(purchasePrice)
                .salesPrice(salesPrice)
                .category(category)
                .inventory(inventory)
                .active(active)
                .creationDate(creationDate)
                .description(description)
                .build();
    }
}