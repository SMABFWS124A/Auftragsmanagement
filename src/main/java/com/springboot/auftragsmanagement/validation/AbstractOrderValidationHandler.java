package com.springboot.auftragsmanagement.validation;

import com.springboot.auftragsmanagement.dto.OrderDto;

public abstract class AbstractOrderValidationHandler implements OrderValidationHandler {

    private OrderValidationHandler next;

    @Override
    public OrderValidationHandler linkWith(OrderValidationHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public void validate(OrderDto orderDto) {
        doValidate(orderDto);
        if (next != null) {
            next.validate(orderDto);
        }
    }

    protected abstract void doValidate(OrderDto orderDto);
}