package com.springboot.auftragsmanagement.validation;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.exception.StockExceededException;
import com.springboot.auftragsmanagement.service.ArticleService;
import org.springframework.stereotype.Component;

@Component
public class StockLevelValidationHandler extends AbstractOrderValidationHandler {

    private final ArticleService articleService;

    public StockLevelValidationHandler(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    protected void doValidate(OrderDto orderDto) {
        for (OrderItemDto item : orderDto.items()) {
            boolean available = articleService.checkStockLevel(item.articleId(), item.quantity());
            if (!available) {
                throw new StockExceededException(
                        "Nicht genügend Bestand für Artikel mit ID " + item.articleId() +
                                ". Benötigt: " + item.quantity()
                );
            }
        }
    }
}