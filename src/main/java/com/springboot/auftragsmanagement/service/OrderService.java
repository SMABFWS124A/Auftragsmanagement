package com.springboot.auftragsmanagement.service;

import com.springboot.auftragsmanagement.dto.OrderDto;
import java.util.List;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);
    List<OrderDto> getAllOrders();
    OrderDto deliverOrder(Long orderId);
}