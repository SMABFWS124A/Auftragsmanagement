package com.springboot.auftragsmanagement.facade;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.exception.StockExceededException;
import com.springboot.auftragsmanagement.service.ArticleService;
import com.springboot.auftragsmanagement.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderFacade {

    private final OrderService orderService;
    private final ArticleService articleService;

    public OrderFacade(OrderService orderService, ArticleService articleService) {
        this.orderService = orderService;
        this.articleService = articleService;
    }

    @Transactional
    public OrderDto placeOrderWithInventoryUpdate(OrderDto orderDto) {
        validateStock(orderDto.items());

        OrderDto savedOrder = orderService.createOrder(orderDto);
        orderDto.items().forEach(item -> articleService.updateInventory(item.articleId(), -item.quantity()));

        return savedOrder;
    }

    public List<OrderDto> listOrders() {
        return orderService.getAllOrders();
    }

    public OrderDto deliverOrder(Long id) {
        return orderService.deliverOrder(id);
    }

    public void deleteDeliveredOrder(Long id) {
        orderService.deleteDeliveredOrder(id);
    }

    private void validateStock(List<OrderItemDto> items) {
        for (OrderItemDto item : items) {
            boolean available = articleService.checkStockLevel(item.articleId(), item.quantity());
            if (!available) {
                throw new StockExceededException(
                        "Nicht genügend Bestand für Artikel mit ID " + item.articleId() +
                                ". Benötigt: " + item.quantity()
                );
            }
        }
    }
}
