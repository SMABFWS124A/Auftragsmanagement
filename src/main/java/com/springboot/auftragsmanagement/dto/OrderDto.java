package com.springboot.auftragsmanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        Long customerId,
        LocalDateTime orderDate,
        String status,
        double totalAmount,
        List<OrderItemDto> items
) {}