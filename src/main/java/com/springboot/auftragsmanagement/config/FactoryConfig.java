package com.springboot.auftragsmanagement.config;

import com.springboot.auftragsmanagement.factory.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfig {

    @Bean
    public SupplierDtoFactory supplierDtoFactory() {
        return new DefaultSupplierDtoFactory();
    }

    @Bean
    public PurchaseOrderItemFactory purchaseOrderItemFactory() {
        return new DefaultPurchaseOrderItemFactory();
    }

    @Bean
    public ArticleDtoFactory articleDtoFactory() {
        return new DefaultArticleDtoFactory();
    }

    @Bean
    public UserDtoFactory userDtoFactory() {
        return new DefaultUserDtoFactory();
    }
}