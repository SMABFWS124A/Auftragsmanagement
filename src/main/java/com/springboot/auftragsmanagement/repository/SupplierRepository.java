package com.springboot.auftragsmanagement.repository;

import com.springboot.auftragsmanagement.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}