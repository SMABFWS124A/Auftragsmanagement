package com.springboot.auftragsmanagement.strategy;

import com.springboot.auftragsmanagement.dto.OrderDto;

/**
 * Strategy interface for calculating order totals. New pricing models (e.g. discounts,
 * taxes, surcharges) can be plugged in by implementing this contract.
 */
public interface OrderPricingStrategy {

    /**
     * @param orderDto the order that should be evaluated
     * @return {@code true} if this strategy should be applied for the given order
     */
    boolean supports(OrderDto orderDto);

    /**
     * Calculates the total amount for the given order.
     *
     * @param orderDto the order to price
     * @return the calculated total amount
     */
    double calculateTotal(OrderDto orderDto);
}