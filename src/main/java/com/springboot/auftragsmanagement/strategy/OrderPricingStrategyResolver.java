package com.springboot.auftragsmanagement.strategy;

import com.springboot.auftragsmanagement.dto.OrderDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;


@Component
public class OrderPricingStrategyResolver {

    private final List<OrderPricingStrategy> strategies;

    public OrderPricingStrategyResolver(List<OrderPricingStrategy> strategies) {
        this.strategies = strategies.stream()
                .sorted(Comparator.comparingInt(strategy -> StrategyOrderExtractor.getOrder(strategy)))
                .toList();
    }

    public double calculateTotal(OrderDto orderDto) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(orderDto))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No pricing strategy available"))
                .calculateTotal(orderDto);
    }
}