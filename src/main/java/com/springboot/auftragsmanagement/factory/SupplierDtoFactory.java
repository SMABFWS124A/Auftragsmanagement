package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.SupplierDto;
import com.springboot.auftragsmanagement.entity.Supplier;

public interface SupplierDtoFactory {
    Supplier createSupplierEntity(SupplierDto dto);
    SupplierDto createSupplierDto(Supplier entity);
    void updateSupplierEntity(Supplier entity, SupplierDto dto);
}