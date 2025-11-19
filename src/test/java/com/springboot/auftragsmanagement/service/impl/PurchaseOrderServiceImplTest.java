package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.PurchaseOrderDto;
import com.springboot.auftragsmanagement.dto.PurchaseOrderItemDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.entity.PurchaseOrder;
import com.springboot.auftragsmanagement.entity.PurchaseOrderItem;
import com.springboot.auftragsmanagement.entity.Supplier;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import com.springboot.auftragsmanagement.repository.PurchaseOrderRepository;
import com.springboot.auftragsmanagement.repository.SupplierRepository;
import com.springboot.auftragsmanagement.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceImplTest {

    @Mock private PurchaseOrderRepository orderRepository;
    @Mock private SupplierRepository supplierRepository;
    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleService articleService;

    @InjectMocks private PurchaseOrderServiceImpl purchaseOrderService;

    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2025, 1, 1, 10, 0);
    private static final String TEST_SUPPLIER_NAME = "Test Supplier";

    // Entspricht PurchaseOrderItemDto(Long articleId, int quantity, double unitPrice)
    private PurchaseOrderItemDto createPurchaseItemDto(Long articleId, int quantity, double unitPrice) {
        return new PurchaseOrderItemDto(articleId, quantity, unitPrice);
    }

    /**
     * KORRIGIERT: verwendet orderId anstelle von id.
     * PurchaseOrderDto(Long orderId, Long supplierId, String supplierName, LocalDateTime orderDate, String status, double totalAmount, List<PurchaseOrderItemDto> items)
     */
    private PurchaseOrderDto createPurchaseOrderDto(Long orderId, Long supplierId, String status, Double totalAmount, List<PurchaseOrderItemDto> items) {
        return new PurchaseOrderDto(orderId, supplierId, TEST_SUPPLIER_NAME, TEST_DATE, status, totalAmount, items);
    }

    private PurchaseOrder createTestPurchaseOrderEntity(Long id, String status, Supplier supplier, double totalAmount) {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(id);
        order.setSupplier(supplier);
        order.setStatus(status);
        order.setTotalAmount(totalAmount);
        order.setOrderDate(TEST_DATE);
        order.setItems(Collections.emptyList());
        return order;
    }


    // ===================================
    // 1. Tests für createOrder
    // ===================================

    @Test
    void createOrder_ShouldCreateSuccessfully() {
        // Arrange
        Long supplierId = 1L;
        Long articleId = 2L;
        double expectedTotal = 50.0;
        int quantity = 10;
        double unitPrice = 5.0;

        PurchaseOrderItemDto itemDto = createPurchaseItemDto(articleId, quantity, unitPrice);
        PurchaseOrderDto inputDto = createPurchaseOrderDto(null, supplierId, "NEU", 0.0, List.of(itemDto));

        Supplier supplier = new Supplier();
        supplier.setId(supplierId);

        Article article = new Article();
        article.setId(articleId);

        PurchaseOrder savedOrder = createTestPurchaseOrderEntity(10L, "BESTELLT", supplier, expectedTotal);
        PurchaseOrderItem savedItem = new PurchaseOrderItem();
        savedItem.setArticle(article);
        savedItem.setQuantity(quantity);
        savedItem.setUnitPrice(unitPrice);
        savedOrder.setItems(List.of(savedItem));


        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(orderRepository.save(any(PurchaseOrder.class))).thenReturn(savedOrder);

        // Act
        PurchaseOrderDto result = purchaseOrderService.createOrder(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.orderId()); // KORREKTUR: result.orderId() statt result.id()
        assertEquals(expectedTotal, result.totalAmount(), 0.001);
        assertEquals("BESTELLT", result.status());
        assertEquals(TEST_DATE, result.orderDate());
        verify(supplierRepository).findById(supplierId);
        verify(articleRepository).findById(articleId);
        verify(orderRepository).save(any(PurchaseOrder.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenSupplierNotFound() {
        Long supplierId = 99L;
        PurchaseOrderDto inputDto = createPurchaseOrderDto(null, supplierId, "NEU", 0.0, Collections.emptyList());

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.createOrder(inputDto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowException_WhenArticleNotFound() {
        Long supplierId = 1L;
        Long articleId = 99L;

        PurchaseOrderItemDto itemDto = createPurchaseItemDto(articleId, 1, 1.0);
        PurchaseOrderDto inputDto = createPurchaseOrderDto(null, supplierId, "NEU", 0.0, List.of(itemDto));

        Supplier supplier = new Supplier();
        supplier.setId(supplierId);

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.createOrder(inputDto));
        verify(orderRepository, never()).save(any());
    }

    // ===================================
    // 2. Tests für receiveOrder
    // ===================================

    @Test
    void receiveOrder_ShouldIncreaseInventoryAndSetStatusToDelivered() {
        // Arrange
        Long orderId = 1L;
        Long articleId = 5L;
        int quantity = 20;

        Article article = new Article();
        article.setId(articleId);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setArticle(article);
        item.setQuantity(quantity);

        PurchaseOrder order = createTestPurchaseOrderEntity(orderId, "BESTELLT", new Supplier(), 100.0);
        order.setItems(List.of(item));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(PurchaseOrder.class))).thenReturn(order);

        // Act
        PurchaseOrderDto result = purchaseOrderService.receiveOrder(orderId);

        // Assert
        assertEquals("GELIEFERT", order.getStatus());
        assertEquals("GELIEFERT", result.status());
        verify(articleService).updateInventory(articleId, quantity);
        verify(orderRepository).save(order);
    }

    @Test
    void receiveOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.receiveOrder(orderId));
        verify(articleService, never()).updateInventory(anyLong(), anyInt());
    }

    @Test
    void receiveOrder_ShouldThrowException_WhenAlreadyReceived() {
        Long orderId = 1L;
        PurchaseOrder order = createTestPurchaseOrderEntity(orderId, "GELIEFERT", new Supplier(), 100.0);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> purchaseOrderService.receiveOrder(orderId));
        verify(articleService, never()).updateInventory(anyLong(), anyInt());
    }

    // ===================================
    // 3. Tests für getOrderById
    // ===================================

    @Test
    void getOrderById_ShouldReturnDto_WhenFound() {
        Long orderId = 1L;
        Supplier supplier = new Supplier();
        supplier.setId(10L);
        PurchaseOrder order = createTestPurchaseOrderEntity(orderId, "BESTELLT", supplier, 50.0);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        PurchaseOrderDto result = purchaseOrderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.orderId()); // KORREKTUR: result.orderId() statt result.id()
        assertEquals(10L, result.supplierId());
    }

    @Test
    void getOrderById_ShouldThrowException_WhenNotFound() {
        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.getOrderById(orderId));
    }

    // ===================================
    // 4. Tests für getAllOrders
    // ===================================

    @Test
    void getAllOrders_ShouldReturnListOfDtos() {
        Supplier supplier = new Supplier();
        supplier.setId(1L);

        PurchaseOrder order1 = createTestPurchaseOrderEntity(1L, "BESTELLT", supplier, 10.0);
        PurchaseOrder order2 = createTestPurchaseOrderEntity(2L, "GELIEFERT", supplier, 20.0);

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<PurchaseOrderDto> result = purchaseOrderService.getAllOrders();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).orderId()); // KORREKTUR: result.get(0).orderId() statt result.get(0).id()
        assertEquals("GELIEFERT", result.get(1).status());
        verify(orderRepository).findAll();
    }

    // ===================================
    // 5. Tests für updateOrderStatus
    // ===================================

    @Test
    void updateOrderStatus_ShouldUpdateStatusAndReturnDto() {
        Long orderId = 1L;
        String newStatus = "STORNIERT";
        Supplier supplier = new Supplier();
        supplier.setId(1L);

        PurchaseOrder order = createTestPurchaseOrderEntity(orderId, "BESTELLT", supplier, 100.0);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(PurchaseOrder.class))).thenReturn(order);

        PurchaseOrderDto result = purchaseOrderService.updateOrderStatus(orderId, newStatus);

        assertEquals(newStatus, order.getStatus());
        assertEquals(newStatus, result.status());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_ShouldThrowException_WhenNotFound() {
        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.updateOrderStatus(orderId, "STATUS"));
        verify(orderRepository, never()).save(any());
    }

    // ===================================
    // 6. Tests für deletePurchaseOrder
    // ===================================

    @Test
    void deletePurchaseOrder_ShouldDeleteSuccessfully() {
        Long orderId = 1L;
        PurchaseOrder order = createTestPurchaseOrderEntity(orderId, "BESTELLT", new Supplier(), 50.0);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        purchaseOrderService.deletePurchaseOrder(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).delete(order);
    }

    @Test
    void deletePurchaseOrder_ShouldThrowException_WhenNotFound() {
        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.deletePurchaseOrder(orderId));
        verify(orderRepository, never()).delete(any());
    }
}