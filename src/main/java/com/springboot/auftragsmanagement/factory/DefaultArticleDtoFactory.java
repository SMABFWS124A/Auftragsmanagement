package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.ArticleDto;

import java.time.LocalDateTime;

public class DefaultArticleDtoFactory implements ArticleDtoFactory {
    @Override
    public ArticleDto createArticle(Long id, String articleNumber, String articleName, double purchasePrice, double salesPrice,
                                    String category, int inventory, boolean active, LocalDateTime creationDate, String description) {
        return new ArticleDto(id, articleNumber, articleName, purchasePrice, salesPrice, category, inventory, active, creationDate, description);
    }
}