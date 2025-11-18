package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.ArticleDto;

import java.time.LocalDateTime;

public interface ArticleDtoFactory {
    ArticleDto createArticle(Long id, String articleNumber, String articleName, double purchasePrice, double salesPrice,
                             String category, int inventory, boolean active, LocalDateTime creationDate, String description);
}