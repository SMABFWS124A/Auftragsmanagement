package com.springboot.auftragsmanagement.strategy;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Default strategy that simply sums up all order lines. It acts as the fallback
 * for every order that is not handled by a more specific strategy.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class StandardOrderPricingStrategy implements OrderPricingStrategy {

    @Override
    public boolean supports(OrderDto orderDto) {
        return true;
    }

    @Override
    public double calculateTotal(OrderDto orderDto) {
        return orderDto.items().stream()
                .mapToDouble(item -> item.quantity() * item.unitPrice())
                .sum();
    }

    protected double sumItems(OrderDto orderDto) {
        return orderDto.items().stream()
                .mapToDouble(this::calculateItemTotal)
                .sum();
    }

    private double calculateItemTotal(OrderItemDto itemDto) {
        return itemDto.quantity() * itemDto.unitPrice();
    }
}