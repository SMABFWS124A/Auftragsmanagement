package com.springboot.auftragsmanagement.service;

import com.springboot.auftragsmanagement.dto.PurchaseOrderDto;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDto createOrder(PurchaseOrderDto purchaseOrderDto);
    PurchaseOrderDto getOrderById(Long id);
    List<PurchaseOrderDto> getAllOrders();
    PurchaseOrderDto updateOrderStatus(Long id, String newStatus);
    PurchaseOrderDto receiveOrder(Long id);
}