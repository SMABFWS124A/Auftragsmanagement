package com.springboot.auftragsmanagement.repository;

import com.springboot.auftragsmanagement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}