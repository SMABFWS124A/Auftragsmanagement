package com.springboot.auftragsmanagement.config;

import com.springboot.auftragsmanagement.validation.MinimumQuantityValidationHandler;
import com.springboot.auftragsmanagement.validation.OrderValidationHandler;
import com.springboot.auftragsmanagement.validation.StockLevelValidationHandler;
import com.springboot.auftragsmanagement.validation.UniqueArticleValidationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OrderValidationConfig {

    @Bean
    @Primary
    public OrderValidationHandler orderValidationChain(StockLevelValidationHandler stockLevelValidationHandler,
                                                       MinimumQuantityValidationHandler minimumQuantityValidationHandler,
                                                       UniqueArticleValidationHandler uniqueArticleValidationHandler) {
        stockLevelValidationHandler
                .linkWith(minimumQuantityValidationHandler)
                .linkWith(uniqueArticleValidationHandler);

        return stockLevelValidationHandler;
    }
}