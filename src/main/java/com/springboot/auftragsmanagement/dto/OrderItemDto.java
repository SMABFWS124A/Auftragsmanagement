package com.springboot.auftragsmanagement.dto;

/**
 * DTO für ein einzelnes Bestellelement.
 * Wird als Record implementiert, da es eine einfache, unveränderliche (immutable) Datenklasse ist.
 */
public record OrderItemDto(
        Long articleId,
        Integer quantity,
        Double unitPrice
) {}