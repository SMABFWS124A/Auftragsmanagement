package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.PurchaseOrderDto;
import com.springboot.auftragsmanagement.dto.PurchaseOrderItemDto;
import com.springboot.auftragsmanagement.entity.Article;
import com.springboot.auftragsmanagement.entity.PurchaseOrder;
import com.springboot.auftragsmanagement.entity.PurchaseOrderItem;
import com.springboot.auftragsmanagement.entity.Supplier;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.factory.PurchaseOrderFactory;
import com.springboot.auftragsmanagement.factory.PurchaseOrderItemFactory;
import com.springboot.auftragsmanagement.repository.ArticleRepository;
import com.springboot.auftragsmanagement.repository.PurchaseOrderRepository;
import com.springboot.auftragsmanagement.repository.SupplierRepository;
import com.springboot.auftragsmanagement.service.ArticleService;
import com.springboot.auftragsmanagement.service.PurchaseOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final ArticleRepository articleRepository;
    private final ArticleService articleService;
    private final PurchaseOrderFactory purchaseOrderFactory;
    private final PurchaseOrderItemFactory purchaseOrderItemFactory;

    public PurchaseOrderServiceImpl(
            PurchaseOrderRepository orderRepository,
            SupplierRepository supplierRepository,
            ArticleRepository articleRepository,
            ArticleService articleService,
            PurchaseOrderFactory purchaseOrderFactory,
            PurchaseOrderItemFactory purchaseOrderItemFactory
    ) {
        this.orderRepository = orderRepository;
        this.supplierRepository = supplierRepository;
        this.articleRepository = articleRepository;
        this.articleService = articleService;
        this.purchaseOrderFactory = purchaseOrderFactory;
        this.purchaseOrderItemFactory = purchaseOrderItemFactory;
    }

    private PurchaseOrderItemDto mapItemToDto(PurchaseOrderItem item) {
        return purchaseOrderItemFactory.createPurchaseOrderItem(
                item.getArticle().getId(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }

    // KORRIGIERTE mapToDto Methode, die die erweiterte Factory nutzt
    private PurchaseOrderDto mapToDto(PurchaseOrder entity) {
        List<PurchaseOrderItemDto> itemDtos = entity.getItems() != null
                ? entity.getItems().stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList())
                : List.of();

        // **VERWENDUNG DER ERWEITERTEN FACTORY-METHODE**
        return purchaseOrderFactory.createPurchaseOrder(
                entity.getSupplier().getId(),
                entity.getSupplier().getName(),   // <-- NEU: Supplier Name
                entity.getOrderDate(),            // <-- NEU: Order Date
                entity.getId(),
                entity.getStatus(),
                entity.getTotalAmount(),
                itemDtos
        );
    }

    @Override
    @Transactional
    public PurchaseOrderDto createOrder(PurchaseOrderDto orderDto) {
        Supplier supplier = supplierRepository.findById(orderDto.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", orderDto.supplierId()));

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        order.setStatus("BESTELLT");
        // HINWEIS: Hier sollte order.setOrderDate(LocalDateTime.now()) gesetzt werden,
        // falls nicht bereits in der Entity mittels @CreationTimestamp oder ähnlichem erfolgt.

        List<PurchaseOrderItem> items = orderDto.items().stream().map(itemDto -> {
            Article article = articleRepository.findById(itemDto.articleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Article", "id", itemDto.articleId()));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(order);
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

        PurchaseOrder savedOrder = orderRepository.save(order);

        return mapToDto(savedOrder);
    }

    @Override
    public PurchaseOrderDto getOrderById(Long id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        return mapToDto(order);
    }

    @Override
    public List<PurchaseOrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseOrderDto updateOrderStatus(Long id, String newStatus) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        order.setStatus(newStatus);

        PurchaseOrder updatedOrder = orderRepository.save(order);
        return mapToDto(updatedOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderDto receiveOrder(Long id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        if ("GELIEFERT".equals(order.getStatus())) {
            throw new IllegalArgumentException("Die Bestellung mit ID " + id + " wurde bereits geliefert.");
        }

        for (PurchaseOrderItem item : order.getItems()) {
            articleService.updateInventory(item.getArticle().getId(), item.getQuantity());
        }

        order.setStatus("GELIEFERT");

        PurchaseOrder savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }

    @Override
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        orderRepository.delete(order);
    }
}