package com.springboot.auftragsmanagement.event;

import com.springboot.auftragsmanagement.entity.Order;
import com.springboot.auftragsmanagement.entity.OrderItem;
import com.springboot.auftragsmanagement.service.ArticleService;
import org.springframework.stereotype.Component;

/**
 * Observer that keeps the inventory in sync whenever an order is delivered.
 */
@Component
public class InventoryAdjustingOrderEventListener implements OrderEventListener {

    private final ArticleService articleService;

    public InventoryAdjustingOrderEventListener(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void onOrderDelivered(Order order) {
        for (OrderItem item : order.getItems()) {
            articleService.updateInventory(item.getArticle().getId(), -item.getQuantity());
        }
    }
}