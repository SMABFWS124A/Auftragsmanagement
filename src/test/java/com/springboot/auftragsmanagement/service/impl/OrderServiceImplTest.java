package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleService articleService;

    // Der Mapper fehlt in den Mocks, aber der Service verwendet interne Helfermethoden
    // Daher müssen wir die Entity-Rückgabe so vorbereiten, dass der Service das DTO korrekt mappen kann.
    @InjectMocks private OrderServiceImpl orderService;

    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2025, 1, 1, 10, 0);

    /**
     * Entspricht OrderItemDto(Long articleId, int quantity, double unitPrice)
     */
    private OrderItemDto createItemDto(Long articleId, int quantity, double unitPrice) {
        return new OrderItemDto(articleId, quantity, unitPrice);
    }

    /**
     * Entspricht OrderDto(Long id, Long customerId, LocalDateTime orderDate, String status, double totalAmount, List<OrderItemDto> items)
     */
    private OrderDto createOrderDto(Long id, Long customerId, String status, double totalAmount, List<OrderItemDto> items) {
        return new OrderDto(id, customerId, TEST_DATE, status, totalAmount, items);
    }

    /**
     * Helfer zur Erstellung einer minimalen Order Entity.
     */
    private Order createTestOrderEntity(Long id, String status, User customer) {
        Order order = new Order();
        order.setId(id);
        order.setCustomer(customer);
        order.setStatus(status);
        order.setOrderDate(TEST_DATE);
        return order;
    }

    // --- 1. Test: createOrder ---

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

        User customer = new User();
        customer.setId(customerId);

        Article article = new Article();
        article.setId(articleId);

        Order savedOrder = createTestOrderEntity(100L, "NEU", customer);
        savedOrder.setTotalAmount(expectedTotal);

        OrderItem orderItem = new OrderItem();
        orderItem.setArticle(article);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        savedOrder.setItems(List.of(orderItem));

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        OrderDto result = orderService.createOrder(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals("NEU", result.status());
        assertEquals(expectedTotal, result.totalAmount(), 0.001);
        assertEquals(1, result.items().size());
        assertEquals(articleId, result.items().get(0).articleId());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenCustomerNotFound() {
        // Arrange
        Long customerId = 99L;
        OrderDto inputDto = createOrderDto(null, customerId, null, 0.0, List.of(createItemDto(2L, 1, 1.0)));

        when(userRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(inputDto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowException_WhenArticleNotFound() {
        // Arrange
        Long customerId = 1L;
        Long articleId = 99L;
        OrderDto inputDto = createOrderDto(null, customerId, null, 0.0, List.of(createItemDto(articleId, 1, 1.0)));

        when(userRepository.findById(customerId)).thenReturn(Optional.of(new User()));
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(inputDto));
        verify(orderRepository, never()).save(any());
    }

    // --- 2. Test: getAllOrders ---

    @Test
    void getAllOrders_ShouldReturnListOfDtos() {
        // Arrange
        User customer = new User();
        customer.setId(1L);
        Article article = new Article();
        article.setId(10L);

        Order order1 = createTestOrderEntity(1L, "NEU", customer);
        Order order2 = createTestOrderEntity(2L, "GELIEFERT", customer);

        OrderItem item1 = new OrderItem();
        item1.setArticle(article);
        item1.setQuantity(5);
        order1.setItems(List.of(item1));
        order1.setTotalAmount(50.0);

        order2.setItems(List.of());

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        // Act
        List<OrderDto> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("GELIEFERT", result.get(1).status());
        assertEquals(1, result.get(0).items().size());
        verify(orderRepository).findAll();
    }

    // --- 3. Test: deliverOrder ---

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

        Order order = createTestOrderEntity(orderId, "NEU", new User());
        order.setItems(List.of(item));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        // articleService.updateInventory() soll erfolgreich sein (nichts tun)

        // Act
        orderService.deliverOrder(orderId);

        // Assert
        assertEquals("GELIEFERT", order.getStatus());
        // Prüfen der Bestandsreduzierung (negative Menge)
        verify(articleService).updateInventory(articleId, -quantity);
        verify(orderRepository).save(order);
    }

    @Test
    void deliverOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.deliverOrder(orderId));
        verify(articleService, never()).updateInventory(anyLong(), anyInt());
    }

    @Test
    void deliverOrder_ShouldThrowException_WhenAlreadyDelivered() {
        Long orderId = 1L;
        Order order = createTestOrderEntity(orderId, "GELIEFERT", new User());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.deliverOrder(orderId));
        verify(articleService, never()).updateInventory(anyLong(), anyInt());
    }

    @Test
    void deliverOrder_ShouldThrowStockExceededException_AndLeadToRollback() {
        // Arrange
        Long orderId = 1L;
        Long articleId = 2L;
        int quantity = 5;

        Article article = new Article();
        article.setId(articleId);

        OrderItem item = new OrderItem();
        item.setArticle(article);
        item.setQuantity(quantity);

        Order order = createTestOrderEntity(orderId, "NEU", new User());
        order.setItems(List.of(item));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        // Simulieren, dass updateInventory eine StockExceededException wirft
        doThrow(new StockExceededException("Not enough stock"))
                .when(articleService).updateInventory(articleId, -quantity);

        // Act & Assert
        // Die Exception wird direkt weitergeleitet
        assertThrows(StockExceededException.class, () -> orderService.deliverOrder(orderId));

        // Der Order-Status darf NICHT auf GELIEFERT geändert werden, da die Transaktion fehlschlägt.
        assertEquals("NEU", order.getStatus());
        verify(orderRepository, never()).save(any());
    }

    // --- 4. Test: deleteDeliveredOrder ---

    @Test
    void deleteDeliveredOrder_ShouldDeleteOrderSuccessfully() {
        // Arrange
        Long orderId = 1L;
        Order order = createTestOrderEntity(orderId, "GELIEFERT", new User());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        // Rückgabe von delete ist void

        // Act
        orderService.deleteDeliveredOrder(orderId);

        // Assert
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteDeliveredOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteDeliveredOrder(orderId));
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void deleteDeliveredOrder_ShouldThrowException_WhenNotDelivered() {
        // Arrange
        Long orderId = 1L;
        Order order = createTestOrderEntity(orderId, "NEU", new User());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> orderService.deleteDeliveredOrder(orderId));
        verify(orderRepository, never()).delete(any());
    }
}