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
import com.springboot.auftragsmanagement.strategy.OrderPricingStrategyResolver;
import com.springboot.auftragsmanagement.service.workflow.OrderDeliveryWorkflow;
import com.springboot.auftragsmanagement.service.workflow.OrderWorkflowTemplate;
import com.springboot.auftragsmanagement.service.workflow.OrderWorkflowTemplateDependencies;
import com.springboot.auftragsmanagement.event.OrderEventPublisher;

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


        order.setItems(items);
        order.setTotalAmount(orderPricingStrategyResolver.calculateTotal(orderDto));

        Order savedOrder = orderRepository.save(order);
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
            throw new IllegalStateException("Der Auftrag mit ID " + orderId + " kann nur im Status 'GELIEFERT' gelöscht werden.");
        }

        orderRepository.delete(order);
    }
}