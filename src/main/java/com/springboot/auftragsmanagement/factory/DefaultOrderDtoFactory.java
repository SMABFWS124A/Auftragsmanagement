package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.dto.OrderItemDto;
import com.springboot.auftragsmanagement.entity.Order;
import com.springboot.auftragsmanagement.entity.User;
import com.springboot.auftragsmanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DefaultOrderDtoFactory implements OrderDtoFactory {

    private final OrderItemDtoFactory orderItemDtoFactory;
    private final UserRepository userRepository;

    public DefaultOrderDtoFactory(OrderItemDtoFactory orderItemDtoFactory, UserRepository userRepository) {
        this.orderItemDtoFactory = orderItemDtoFactory;
        this.userRepository = userRepository;
    }

    @Override
    public OrderDto createOrder(
            Long id,
            Long customerId,
            LocalDateTime orderDate,
            String status,
            Double totalAmount,
            List<OrderItemDto> items
    ) {
        return new OrderDto(id, customerId, orderDate, status, totalAmount, items);
    }


    @Override
    public OrderDto createOrderDto(Order entity) {
        List<OrderItemDto> itemDtos = orderItemDtoFactory.createOrderItemDtos(entity.getItems());

        return createOrder(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getOrderDate(),
                entity.getStatus(),
                entity.getTotalAmount(),
                itemDtos
        );
    }

    /**
     * Implementierung der Entity-Erstellung (benötigt UserRepository und Item Factory).
     */
    @Override
    public Order createOrderEntity(OrderDto dto) {
        Order entity = new Order();

        if (dto.id() != null) {
            entity.setId(dto.id());
        }

        User customer = userRepository.findById(dto.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + dto.customerId()));
        entity.setCustomer(customer);

        entity.setOrderDate(dto.orderDate() != null ? dto.orderDate() : LocalDateTime.now());
        entity.setStatus(dto.status());
        entity.setTotalAmount(dto.totalAmount() != null ? dto.totalAmount() : 0.0);

        if (dto.items() != null) {
            entity.setItems(
                    dto.items().stream()
                            .map(itemDto -> orderItemDtoFactory.createOrderItemEntity(itemDto, entity))
                            .collect(Collectors.toList())
            );
        } else {
            entity.setItems(List.of());
        }

        return entity;
    }


    @Override
    public void updateOrderEntity(Order existingEntity, OrderDto dto) {
        if (!existingEntity.getCustomer().getId().equals(dto.customerId())) {
            User customer = userRepository.findById(dto.customerId())
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + dto.customerId()));
            existingEntity.setCustomer(customer);
        }

        existingEntity.setOrderDate(dto.orderDate());
        existingEntity.setStatus(dto.status());
        existingEntity.setTotalAmount(dto.totalAmount());

    }
}