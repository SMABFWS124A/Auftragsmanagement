package com.springboot.auftragsmanagement.validation;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.exception.OrderValidationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderStructureValidationHandler extends AbstractOrderValidationHandler {

    @Override
    protected void doValidate(OrderDto orderDto) {
        if (orderDto.customerId() == null) {
            throw new OrderValidationException("Kunde muss ausgewählt sein.");
        }

        if (orderDto.items() == null || orderDto.items().isEmpty()) {
            throw new OrderValidationException("Mindestens eine Position ist erforderlich.");
        }

        for (OrderItemDto item : orderDto.items()) {
            if (item == null) {
                throw new OrderValidationException("Position darf nicht leer sein.");
            }
            if (item.articleId() == null) {
                throw new OrderValidationException("Artikel-ID darf nicht leer sein.");
            }
            if (Objects.isNull(item.unitPrice()) || item.unitPrice() <= 0) {
                throw new OrderValidationException("Preis pro Stück muss größer als 0 sein.");
            }
        }
    }
}