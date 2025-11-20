package com.springboot.auftragsmanagement.validation;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.exception.OrderValidationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UniqueArticleValidationHandler extends AbstractOrderValidationHandler {

    @Override
    protected void doValidate(OrderDto orderDto) {
        Set<Long> articleIds = new HashSet<>();
        for (OrderItemDto item : orderDto.items()) {
            boolean isNew = articleIds.add(item.articleId());
            if (!isNew) {
                throw new OrderValidationException(
                        "Artikel " + item.articleId() + " darf nur einmal pro Bestellung vorkommen"
                );
            }
        }
    }
}