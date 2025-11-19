package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.dto.PurchaseOrderDto;
import com.springboot.auftragsmanagement.dto.PurchaseOrderItemDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.entity.Order;
import com.springboot.auftragsmanagement.entity.OrderItem;
import com.springboot.auftragsmanagement.entity.User;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.exception.StockExceededException;
import com.springboot.auftragsmanagement.factory.PurchaseOrderFactory;
import com.springboot.auftragsmanagement.factory.PurchaseOrderItemFactory;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import com.springboot.auftragsmanagement.repository.OrderRepository;
import com.springboot.auftragsmanagement.repository.UserRepository;
import com.springboot.auftragsmanagement.service.ArticleService;
import com.springboot.auftragsmanagement.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleService articleService;
    private final PurchaseOrderFactory purchaseOrderFactory;
    private final PurchaseOrderItemFactory purchaseOrderItemFactory;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ArticleRepository articleRepository,
            ArticleService articleService,
            PurchaseOrderFactory purchaseOrderFactory,
            PurchaseOrderItemFactory purchaseOrderItemFactory) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.articleService = articleService;
        this.purchaseOrderFactory = purchaseOrderFactory;
        this.purchaseOrderItemFactory = purchaseOrderItemFactory;
    }

    private OrderItemDto mapItemToDto(OrderItem item) {
        return OrderItemDto.builder()
                .articleId(item.getArticle().getId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }

    private OrderDto mapToDto(Order entity) {
        List<OrderItemDto> itemDtos = entity.getItems() != null
                ? entity.getItems().stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList())
                : List.of();

        return OrderDto.builder()
                .id(entity.getId())
                .customerId(entity.getCustomer().getId())
                .orderDate(entity.getOrderDate())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .items(itemDtos)
                .build();
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        User customer = userRepository.findById(orderDto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", orderDto.customerId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus("NEU");

        List<OrderItem> items = orderDto.items().stream().map(itemDto -> {
            Article article = articleRepository.findById(itemDto.articleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Article", "id", itemDto.articleId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setArticle(article);
            item.setQuantity(itemDto.quantity());
            item.setUnitPrice(itemDto.unitPrice());

            return item;
        }).toList();

        double calculatedTotal = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        order.setItems(items);
        order.setTotalAmount(calculatedTotal);

        Order savedOrder = orderRepository.save(order);

        return mapToDto(savedOrder);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if ("GELIEFERT".equals(order.getStatus())) {
            throw new IllegalArgumentException("Der Auftrag mit ID " + orderId + " wurde bereits geliefert.");
        }

        for (OrderItem item : order.getItems()) {
            articleService.updateInventory(item.getArticle().getId(), -item.getQuantity());
        }

        order.setStatus("GELIEFERT");
        Order savedOrder = orderRepository.save(order);

        return mapToDto(savedOrder);
    }

    @Override
    @Transactional
    public void deleteDeliveredOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!"GELIEFERT".equals(order.getStatus())) {
            throw new IllegalStateException("Der Auftrag mit ID " + orderId + " kann nur im Status 'GELIEFERT' gelöscht werden.");
        }

        orderRepository.delete(order);
    }
}