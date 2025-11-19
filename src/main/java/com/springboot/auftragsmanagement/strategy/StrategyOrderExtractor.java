package com.springboot.auftragsmanagement.strategy;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.annotation.OrderUtils;

final class StrategyOrderExtractor {

    private StrategyOrderExtractor() {
    }

    static int getOrder(OrderPricingStrategy strategy) {
        Order order = OrderUtils.getOrder(strategy.getClass());
        if (order != null) {
            return order.value();
        }
        return strategy instanceof Ordered ordered
                ? ordered.getOrder()
                : Ordered.LOWEST_PRECEDENCE;
    }
}