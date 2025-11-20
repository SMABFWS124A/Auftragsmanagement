package com.springboot.auftragsmanagement.service.workflow;

import com.springboot.auftragsmanagement.entity.Order;
import com.springboot.auftragsmanagement.event.OrderEventPublisher;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.repository.OrderRepository;

public abstract class OrderWorkflowTemplate {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    protected OrderWorkflowTemplate(OrderRepository orderRepository, OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    public Order execute(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        validate(order);
        mutate(order);
        Order savedOrder = orderRepository.save(order);
        afterSave(savedOrder);

        return savedOrder;
    }

    protected void validate(Order order) {
    }

    protected abstract void mutate(Order order);

    protected void afterSave(Order order) {
    }

    protected OrderEventPublisher publisher() {
        return orderEventPublisher;
    }
}