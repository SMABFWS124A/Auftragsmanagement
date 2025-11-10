package com.springboot.auftragsmanagement.mapper;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import com.springboot.auftragsmanagement.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {
    public ArticleDto toDto(Article article){
        return new ArticleDto(
                article.getId(),
                article.getArticleNumber(),
                article.getArticleName(),
                article.getPurchasePrice(),
                article.getSalesPrice(),
                article.getCategory(),
                article.getInventory(),
                article.isActive(),
                article.getCreationDate(),
                article.getDescription()
        );
    }

    public Article toEntity (ArticleDto articleDto){
        return new Article(
                articleDto.id(),
                articleDto.articleNumber(),
                articleDto.articleName(),
                articleDto.purchasePrice(),
                articleDto.salesPrice(),
                articleDto.category(),
                articleDto.inventory(),
                articleDto.active(),
                articleDto.creationDate(),
                articleDto.description()
        );
    }
}