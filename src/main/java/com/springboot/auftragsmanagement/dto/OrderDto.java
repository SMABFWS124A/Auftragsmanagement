package com.springboot.auftragsmanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO für eine Kundenbestellung.
 * Wird als Record implementiert, da es eine einfache, unveränderliche (immutable) Datenklasse ist.
 */
public record OrderDto(
        Long id,
        Long customerId,
        LocalDateTime orderDate,
        String status,
        Double totalAmount,
        List<OrderItemDto> items
) {}