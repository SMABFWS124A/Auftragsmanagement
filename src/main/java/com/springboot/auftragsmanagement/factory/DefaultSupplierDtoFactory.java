package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.SupplierDto;
import com.springboot.auftragsmanagement.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class DefaultSupplierDtoFactory implements SupplierDtoFactory {

    @Override
    public Supplier createSupplierEntity(SupplierDto dto) {
        Supplier entity = new Supplier();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setContactPerson(dto.getContactPerson());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        return entity;
    }

    @Override
    public SupplierDto createSupplierDto(Supplier entity) {
        SupplierDto dto = new SupplierDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContactPerson(entity.getContactPerson());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setAddress(entity.getAddress());
        return dto;
    }

    @Override
    public void updateSupplierEntity(Supplier entity, SupplierDto dto) {
        entity.setName(dto.getName());
        entity.setContactPerson(dto.getContactPerson());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
    }
}