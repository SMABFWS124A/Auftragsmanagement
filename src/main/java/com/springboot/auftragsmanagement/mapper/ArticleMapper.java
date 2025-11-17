package com.springboot.auftragsmanagement.mapper;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import com.springboot.auftragsmanagement.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {

    public ArticleDto toDto(Article entity) {
        return new ArticleDto(
                entity.getId(),
                entity.getArticleNumber(),
                entity.getArticleName(),
                entity.getPurchasePrice(),
                entity.getSalesPrice(),
                entity.getCategory(),
                entity.getInventory(),
                entity.isActive(),
                entity.getCreationDate(),
                entity.getDescription()
        );
    }

    public Article toEntity (ArticleDto dto){
        Article entity = new Article();

        if (dto.id() != null) {
            entity.setId(dto.id());
            entity.setCreationDate(dto.creationDate());
        }

        entity.setArticleNumber(dto.articleNumber());
        entity.setArticleName(dto.articleName());
        entity.setPurchasePrice(dto.purchasePrice());
        entity.setSalesPrice(dto.salesPrice());
        entity.setCategory(dto.category());
        entity.setInventory(dto.inventory());
        entity.setActive(dto.active());
        entity.setDescription(dto.description());

        return entity;
    }
}