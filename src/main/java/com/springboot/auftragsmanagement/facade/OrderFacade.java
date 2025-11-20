package com.springboot.auftragsmanagement.facade;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.service.ArticleService;
import com.springboot.auftragsmanagement.service.OrderService;
import com.springboot.auftragsmanagement.validation.OrderValidationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderFacade {

    private final OrderService orderService;
    private final ArticleService articleService;
    private final OrderValidationHandler orderValidationHandler;

    public OrderFacade(OrderService orderService, ArticleService articleService,
                       OrderValidationHandler orderValidationHandler) {
        this.orderService = orderService;
        this.articleService = articleService;
        this.orderValidationHandler = orderValidationHandler;
    }

    @Transactional
    public OrderDto placeOrderWithInventoryUpdate(OrderDto orderDto) {
        orderValidationHandler.validate(orderDto);

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
}