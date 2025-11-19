package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.entity.Order;
import com.springboot.auftragsmanagement.entity.OrderItem;

import java.util.List;

public interface OrderItemDtoFactory {

    OrderItemDto createOrderItem(Long articleId, Integer quantity, Double unitPrice);
    OrderItemDto createOrderItemDto(OrderItem entity);
    OrderItem createOrderItemEntity(OrderItemDto dto, Order parentOrder);
    List<OrderItemDto> createOrderItemDtos(List<OrderItem> entities);
}