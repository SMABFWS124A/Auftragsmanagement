package com.springboot.auftragsmanagement.validation;

import com.springboot.auftragsmanagement.dto.OrderDto;

public interface OrderValidationHandler {

    void validate(OrderDto orderDto);

    OrderValidationHandler linkWith(OrderValidationHandler next);
}