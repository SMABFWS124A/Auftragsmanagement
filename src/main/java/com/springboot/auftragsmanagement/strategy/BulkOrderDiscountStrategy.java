package com.springboot.auftragsmanagement.strategy;

import com.springboot.auftragsmanagement.dto.OrderDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Applies a discount for orders with large quantities to demonstrate the Strategy pattern.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BulkOrderDiscountStrategy extends StandardOrderPricingStrategy {

    private static final int MIN_QUANTITY_FOR_DISCOUNT = 50;
    private static final double DISCOUNT_FACTOR = 0.9; // 10 % Rabatt

    @Override
    public boolean supports(OrderDto orderDto) {
        int totalQuantity = orderDto.items().stream()
                .mapToInt(item -> item.quantity())
                .sum();
        return totalQuantity >= MIN_QUANTITY_FOR_DISCOUNT;
    }

    @Override
    public double calculateTotal(OrderDto orderDto) {
        double baseTotal = sumItems(orderDto);
        return baseTotal * DISCOUNT_FACTOR;
    }
}
