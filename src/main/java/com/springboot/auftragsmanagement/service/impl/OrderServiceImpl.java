package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.entity.Order;
import com.springboot.auftragsmanagement.entity.OrderItem;
import com.springboot.auftragsmanagement.entity.User;
import com.springboot.auftragsmanagement.event.OrderEventPublisher;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import com.springboot.auftragsmanagement.repository.OrderRepository;
import com.springboot.auftragsmanagement.repository.UserRepository;
import com.springboot.auftragsmanagement.service.OrderService;
import com.springboot.auftragsmanagement.strategy.OrderPricingStrategyResolver;
import com.springboot.auftragsmanagement.service.workflow.OrderDeliveryWorkflow;
import com.springboot.auftragsmanagement.service.workflow.OrderWorkflowTemplate;
import com.springboot.auftragsmanagement.service.workflow.OrderWorkflowTemplateDependencies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final OrderPricingStrategyResolver orderPricingStrategyResolver;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderWorkflowTemplate orderDeliveryWorkflow;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ArticleRepository articleRepository,
            OrderPricingStrategyResolver orderPricingStrategyResolver,
            OrderEventPublisher orderEventPublisher) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.orderPricingStrategyResolver = orderPricingStrategyResolver;
        this.orderEventPublisher = orderEventPublisher;
        this.orderDeliveryWorkflow = new OrderDeliveryWorkflow(
                new OrderWorkflowTemplateDependencies(orderRepository, orderEventPublisher));
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

        // 1) Kunde laden (FK customer_id muss gültig sein)
        User customer = userRepository.findById(orderDto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", orderDto.customerId()));

        // 2) Leeren Auftrag aufbauen (noch nicht speichern)
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus("NEU");
        order.setOrderDate(LocalDateTime.now());

        // 3) OrderItems aufbauen und mit Order verknüpfen
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemDto itemDto : orderDto.items()) {
            Article article = articleRepository.findById(itemDto.articleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Article", "id", itemDto.articleId()));

            OrderItem item = new OrderItem();
            item.setArticle(article);
            item.setQuantity(itemDto.quantity());

            // Falls du den Preis vom Frontend schickst:
            // item.setUnitPrice(itemDto.unitPrice());
            // oder sicherheitshalber vom Artikel übernehmen:
            item.setUnitPrice(itemDto.unitPrice() != null
                    ? itemDto.unitPrice()
                    : article.getSalesPrice());

            // Bidirektionale Verknüpfung – wichtig für order_id FK
            order.addItem(item);      // sollte intern item.setOrder(this) machen
            items.add(item);
        }

        order.setItems(items);

        // 4) Gesamtbetrag berechnen (basierend auf DTO oder Items)
        order.setTotalAmount(orderPricingStrategyResolver.calculateTotal(orderDto));

        // 5) Einmal speichern (Order + Items via Cascade)
        Order savedOrder = orderRepository.save(order);

        // 6) Event nach erfolgreichem Speichern publishen
        orderEventPublisher.publishOrderCreated(savedOrder);

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
        Order savedOrder = orderDeliveryWorkflow.execute(orderId);
        return mapToDto(savedOrder);
    }

    @Override
    @Transactional
    public void deleteDeliveredOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!"GELIEFERT".equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Der Auftrag mit ID " + orderId + " kann nur im Status 'GELIEFERT' gelöscht werden.");
        }

        orderRepository.delete(order);
    }
}