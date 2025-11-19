package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.entity.Order;
import com.springboot.auftragsmanagement.entity.OrderItem;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DefaultOrderItemDtoFactory implements OrderItemDtoFactory {

    private final ArticleRepository articleRepository;

    public DefaultOrderItemDtoFactory(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public OrderItemDto createOrderItem(Long articleId, Integer quantity, Double unitPrice) {
        return new OrderItemDto(articleId, quantity, unitPrice);
    }

    @Override
    public OrderItemDto createOrderItemDto(OrderItem entity) {
        return createOrderItem(
                entity.getArticle() != null ? entity.getArticle().getId() : null,
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }

    @Override
    public List<OrderItemDto> createOrderItemDtos(List<OrderItem> entities) {
        return entities.stream()
                .map(this::createOrderItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderItem createOrderItemEntity(OrderItemDto dto, Order parentOrder) {
        OrderItem entity = new OrderItem();
        Article article = articleRepository.findById(dto.articleId())
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + dto.articleId()));

        entity.setOrder(parentOrder);
        entity.setArticle(article);
        entity.setQuantity(dto.quantity());
        entity.setUnitPrice(dto.unitPrice());
        return entity;
    }
}