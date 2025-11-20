package com.springboot.auftragsmanagement.service.workflow;

import com.springboot.auftragsmanagement.entity.Order;

public class OrderDeliveryWorkflow extends OrderWorkflowTemplate {

    public OrderDeliveryWorkflow(OrderWorkflowTemplateDependencies dependencies) {
        super(dependencies.orderRepository(), dependencies.orderEventPublisher());
    }

    @Override
    protected void validate(Order order) {
        if ("GELIEFERT".equals(order.getStatus())) {
            throw new IllegalArgumentException("Der Auftrag mit ID " + order.getId() + " wurde bereits geliefert.");
        }
    }

    @Override
    protected void mutate(Order order) {
        order.setStatus("GELIEFERT");
    }

    @Override
    protected void afterSave(Order order) {
        publisher().publishOrderDelivered(order);
    }
}