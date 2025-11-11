package com.springboot.auftragsmanagement.controller;

import com.springboot.auftragsmanagement.dto.PurchaseOrderDto;
import com.springboot.auftragsmanagement.service.PurchaseOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderDto> createOrder(@RequestBody PurchaseOrderDto orderDto){
        PurchaseOrderDto savedOrder = purchaseOrderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDto> getOrderById(@PathVariable Long id){
        PurchaseOrderDto orderDto = purchaseOrderService.getOrderById(id);
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderDto>> getAllOrders(){
        List<PurchaseOrderDto> orders = purchaseOrderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseOrderDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String newStatus) {

        PurchaseOrderDto updatedOrder = purchaseOrderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrderDto> receiveOrder(@PathVariable Long id){
        PurchaseOrderDto receivedOrder = purchaseOrderService.receiveOrder(id);
        return ResponseEntity.ok(receivedOrder);
    }
}