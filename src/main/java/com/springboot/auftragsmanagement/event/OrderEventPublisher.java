package com.springboot.auftragsmanagement.event;

import com.springboot.auftragsmanagement.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class OrderEventPublisher {

    private final List<OrderEventListener> listeners;

    public OrderEventPublisher(List<OrderEventListener> listeners) {
        this.listeners = List.copyOf(listeners);
    }

    public void publishOrderCreated(Order order) {
        listeners.forEach(listener -> listener.onOrderCreated(order));
    }

    public void publishOrderDelivered(Order order) {
        listeners.forEach(listener -> listener.onOrderDelivered(order));
    }
}