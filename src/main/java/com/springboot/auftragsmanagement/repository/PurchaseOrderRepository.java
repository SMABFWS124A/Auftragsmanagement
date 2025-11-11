package com.springboot.auftragsmanagement.repository;

import com.springboot.auftragsmanagement.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}