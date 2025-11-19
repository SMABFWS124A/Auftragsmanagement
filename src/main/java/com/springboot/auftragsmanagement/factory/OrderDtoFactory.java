package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDtoFactory {

    OrderDto createOrder(
            Long id,
            Long customerId,
            LocalDateTime orderDate,
            String status,
            Double totalAmount,
            List<OrderItemDto> items
    );

    OrderDto createOrderDto(Order entity);
    Order createOrderEntity(OrderDto dto);
    void updateOrderEntity(Order existingEntity, OrderDto dto);
}