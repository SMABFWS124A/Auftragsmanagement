package com.springboot.auftragsmanagement.controller;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto){
        OrderDto savedOrder = orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders(){
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/deliver")
    public ResponseEntity<OrderDto> deliverOrder(@PathVariable Long id){
        OrderDto deliveredOrder = orderService.deliverOrder(id);
        return ResponseEntity.ok(deliveredOrder);
    }
}