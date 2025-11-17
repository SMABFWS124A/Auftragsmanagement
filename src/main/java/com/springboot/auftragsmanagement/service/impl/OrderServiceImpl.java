package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto; // Muss existieren!
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.entity.Order;
import com.springboot.auftragsmanagement.entity.OrderItem;
import com.springboot.auftragsmanagement.entity.User;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.exception.StockExceededException;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import com.springboot.auftragsmanagement.repository.OrderRepository;
import com.springboot.auftragsmanagement.repository.UserRepository;
import com.springboot.auftragsmanagement.service.ArticleService;
import com.springboot.auftragsmanagement.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors; // Für die korrekte DTO-Abbildung benötigt

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleService articleService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ArticleRepository articleRepository,
            ArticleService articleService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.articleService = articleService;
    }

    /**
     * Helferfunktion, um OrderItem-Entity in OrderItemDto umzuwandeln.
     */
    private OrderItemDto mapItemToDto(OrderItem item) {
        return new OrderItemDto(
                item.getArticle().getId(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }

    /**
     * Helferfunktion, um Order-Entity in OrderDto umzuwandeln (inkl. Items).
     */
    private OrderDto mapToDto(Order entity) {
        // Mappe die Liste der OrderItems zu OrderItemDtos
        List<OrderItemDto> itemDtos = entity.getItems() != null
                ? entity.getItems().stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList())
                : List.of(); // Gib leere Liste zurück, falls Items null sind

        return new OrderDto(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getOrderDate(),
                entity.getStatus(),
                entity.getTotalAmount(),
                itemDtos
        );
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
            item.setOrder(order); // Wichtig für bidirektionale Beziehung
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

        // Dank CascadeType.ALL in Order.java werden die Items automatisch mitgespeichert
        Order savedOrder = orderRepository.save(order);

        return mapToDto(savedOrder);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
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
            try {
                // Reduziert den Bestand (Verkauf)
                articleService.updateInventory(item.getArticle().getId(), -item.getQuantity());

            } catch (StockExceededException e) {
                // Führt zum Rollback der Transaktion
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException("Unerwarteter Fehler bei Bestandsupdate für Artikel " + item.getArticle().getArticleName() + ": " + e.getMessage());
            }
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
            throw new IllegalStateException("Der Auftrag mit ID " + orderId + " kann nur im Status 'GELIEFERT' gelöscht werden (aktueller Status: " + order.getStatus() + ").");
        }

        // Durch orphanRemoval=true in Order.java werden die OrderItems ebenfalls gelöscht.
        orderRepository.delete(order);
    }
}