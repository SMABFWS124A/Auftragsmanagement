package com.springboot.auftragsmanagement.dto;
import java.time.LocalDateTime;

public record ArticleDto(Long id,
        String articleNumber,
        String articleName,
        double purchasePrice,
        double salesPrice,
        String category,
        int inventory,
        boolean active,
        LocalDateTime creationDate,
        String description) {
}
