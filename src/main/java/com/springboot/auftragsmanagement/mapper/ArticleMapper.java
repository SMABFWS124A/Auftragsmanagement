package com.springboot.auftragsmanagement.mapper;

import com.springboot.auftragsmanagement.dto.ArticleDto;
import com.springboot.auftragsmanagement.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {

    public ArticleDto toDto(Article entity) {
        return ArticleDto.builder()
                .id(entity.getId())
                .articleNumber(entity.getArticleNumber())
                .articleName(entity.getArticleName())
                .purchasePrice(entity.getPurchasePrice())
                .salesPrice(entity.getSalesPrice())
                .category(entity.getCategory())
                .inventory(entity.getInventory())
                .active(entity.isActive())
                .creationDate(entity.getCreationDate())
                .description(entity.getDescription())
                .build();
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