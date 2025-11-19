package com.springboot.auftragsmanagement.strategy;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;

final class StrategyOrderExtractor {

    private StrategyOrderExtractor() {
    }

    static int getOrder(OrderPricingStrategy strategy) {
        Integer order = OrderUtils.getOrder(strategy.getClass());
        if (order != null) {
            return order;
        }
        return strategy instanceof Ordered ordered
                ? ordered.getOrder()
                : Ordered.LOWEST_PRECEDENCE;
    }
}