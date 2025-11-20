package com.springboot.auftragsmanagement.service.workflow;

import com.springboot.auftragsmanagement.event.OrderEventPublisher;
import com.springboot.auftragsmanagement.repository.OrderRepository;

public record OrderWorkflowTemplateDependencies(
        OrderRepository orderRepository,
        OrderEventPublisher orderEventPublisher
) {
}