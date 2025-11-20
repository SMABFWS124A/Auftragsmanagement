package com.springboot.auftragsmanagement.controller;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.facade.OrderFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderFacade orderFacade;

    public OrderController(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto){
        OrderDto savedOrder = orderFacade.placeOrderWithInventoryUpdate(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders(){
        List<OrderDto> orders = orderFacade.listOrders();
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/deliver")
    public ResponseEntity<OrderDto> deliverOrder(@PathVariable Long id){
        OrderDto deliveredOrder = orderFacade.deliverOrder(id);
        return ResponseEntity.ok(deliveredOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveredOrder(@PathVariable Long id) {
        orderFacade.deleteDeliveredOrder(id);
        return ResponseEntity.noContent().build();
    }
}