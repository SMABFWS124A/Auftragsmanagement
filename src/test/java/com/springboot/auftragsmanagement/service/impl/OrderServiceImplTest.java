package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.entity.Order;
import com.springboot.auftragsmanagement.entity.OrderItem;
import com.springboot.auftragsmanagement.entity.User;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.factory.OrderDtoFactory;
import com.springboot.auftragsmanagement.factory.OrderItemDtoFactory;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import com.springboot.auftragsmanagement.repository.OrderRepository;
import com.springboot.auftragsmanagement.repository.UserRepository;
import com.springboot.auftragsmanagement.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleService articleService;
    @Mock private OrderDtoFactory orderDtoFactory;
    @Mock private OrderItemDtoFactory orderItemDtoFactory;

    @InjectMocks private OrderServiceImpl orderService;

    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2025, 1, 1, 10, 0);

    // KORREKTUR: Verwende direkten Record-Konstruktor
    private OrderItemDto createItemDto(Long articleId, int quantity, double unitPrice) {
        return new OrderItemDto(articleId, quantity, unitPrice);
    }

    // KORREKTUR: Verwende direkten Record-Konstruktor
    private OrderDto createOrderDto(Long id, Long customerId, String status, double totalAmount, List<OrderItemDto> items) {
        return new OrderDto(id, customerId, TEST_DATE, status, totalAmount, items);
    }

    private Order createTestOrderEntity(Long id, String status, User customer) {
        Order order = new Order();
        order.setId(id);
        order.setCustomer(customer);
        order.setStatus(status);
        order.setOrderDate(TEST_DATE);
        order.setItems(List.of());
        return order;
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        // Arrange
        Long customerId = 1L;
        Long articleId = 2L;
        int quantity = 5;
        double unitPrice = 10.0;
        double expectedTotal = 50.0;

        OrderItemDto itemDto = createItemDto(articleId, quantity, unitPrice);
        OrderDto inputDto = createOrderDto(null, customerId, null, 0.0, List.of(itemDto));

        // Output DTO muss das generierte Datum verwenden, das der Service intern setzt
        OrderDto expectedOutputDto = createOrderDto(100L, customerId, "NEU", expectedTotal, List.of(itemDto));

        User customer = new User();
        customer.setId(customerId);

        Article article = new Article();
        article.setId(articleId);

        Order savedOrder = createTestOrderEntity(100L, "NEU", customer);
        // Da die Items vom Service erstellt werden, simulieren wir die Daten in savedOrder für das Assert.
        OrderItem savedItem = new OrderItem();
        savedItem.setArticle(article);
        savedItem.setQuantity(quantity);
        savedItem.setUnitPrice(unitPrice);
        savedOrder.setItems(List.of(savedItem));
        savedOrder.setTotalAmount(expectedTotal);

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // WICHTIG: Die Factory-Mocks werden hier NICHT verwendet, da der Service interne Mapping-Logik verwendet.

        // Act
        OrderDto result = orderService.createOrder(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals("NEU", result.status());
        assertEquals(expectedTotal, result.totalAmount(), 0.001);
        verify(orderRepository).save(any(Order.class));
        verify(userRepository).findById(customerId);
        verify(articleRepository).findById(articleId);
    }

    @Test
    void getAllOrders_ShouldReturnListOfDtos() {
        // Arrange
        User customer = new User();
        customer.setId(1L);

        // Entities mit Daten befüllen
        Order order1 = createTestOrderEntity(1L, "NEU", customer);
        OrderItem item1 = new OrderItem();
        item1.setArticle(new Article());
        item1.setQuantity(1);
        item1.setUnitPrice(10.0);
        order1.setItems(List.of(item1));

        Order order2 = createTestOrderEntity(2L, "GELIEFERT", customer);
        order2.setItems(List.of());

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        // Act
        List<OrderDto> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("GELIEFERT", result.get(1).status());
    }

    @Test
    void deliverOrder_ShouldUpdateInventoryAndStatusSuccessfully() {
        // Arrange
        Long orderId = 1L;
        Long articleId = 2L;
        int quantity = 5;

        Article article = new Article();
        article.setId(articleId);

        OrderItem item = new OrderItem();
        item.setArticle(article);
        item.setQuantity(quantity);
        item.setUnitPrice(10.0);

        Order order = createTestOrderEntity(orderId, "NEU", new User());
        order.setItems(List.of(item));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderDto result = orderService.deliverOrder(orderId);

        // Assert
        assertEquals("GELIEFERT", result.status());
        verify(articleService).updateInventory(articleId, -quantity);
        verify(orderRepository).save(order);
    }

    @Test
    void deleteDeliveredOrder_ShouldDeleteOrderSuccessfully() {
        // Arrange
        Long orderId = 1L;
        Order order = createTestOrderEntity(orderId, "GELIEFERT", new User());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteDeliveredOrder(orderId);

        // Assert
        verify(orderRepository).delete(order);
    }

    @Test
    void deliverOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.deliverOrder(orderId));
    }

    @Test
    void deleteDeliveredOrder_ShouldThrowException_WhenNotDelivered() {
        // Arrange
        Long orderId = 1L;
        Order order = createTestOrderEntity(orderId, "NEU", new User());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> orderService.deleteDeliveredOrder(orderId));
        verify(orderRepository, never()).delete(any(Order.class));
    }
}