package com.springboot.auftragsmanagement.service;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import java.util.List;

public interface ArticleService {
    ArticleDto createArticle(ArticleDto articleDto);

    ArticleDto getArticleById(Long id);

    List<ArticleDto> getAllArticles();

    ArticleDto updateArticle(Long id, ArticleDto articleDto);

    void deleteArticle (Long id);
    double calculateMargin(Long id);

    boolean checkStockLevel(Long id, int requiredQuantity);
    ArticleDto updateInventory(Long id, int inventoryChange);
}