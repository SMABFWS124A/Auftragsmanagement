package com.springboot.auftragsmanagement.validation;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.exception.OrderValidationException;
import org.springframework.stereotype.Component;

@Component
public class MinimumQuantityValidationHandler extends AbstractOrderValidationHandler {

    @Override
    protected void doValidate(OrderDto orderDto) {
        for (OrderItemDto item : orderDto.items()) {
            if (item.quantity() <= 0) {
                throw new OrderValidationException(
                        "Menge für Artikel " + item.articleId() + " muss größer als 0 sein"
                );
            }
        }
    }
}