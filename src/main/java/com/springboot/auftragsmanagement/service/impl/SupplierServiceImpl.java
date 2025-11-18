package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.SupplierDto;
import com.springboot.auftragsmanagement.entity.Supplier;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.factory.SupplierDtoFactory;
import com.springboot.auftragsmanagement.repository.SupplierRepository;
import com.springboot.auftragsmanagement.service.SupplierService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierDtoFactory supplierDtoFactory;

    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierDtoFactory supplierDtoFactory) {
        this.supplierRepository = supplierRepository;
        this.supplierDtoFactory = supplierDtoFactory;
    }

    // --- Service Implementation ---

    @Override
    public SupplierDto createSupplier(SupplierDto supplierDto) {
        Supplier supplier = supplierDtoFactory.createSupplierEntity(supplierDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierDtoFactory.createSupplierDto(savedSupplier);
    }

    @Override
    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return supplierDtoFactory.createSupplierDto(supplier);
    }

    @Override
    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierDtoFactory::createSupplierDto)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        // Update fields using the factory
        supplierDtoFactory.updateSupplierEntity(supplier, supplierDto);

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierDtoFactory.createSupplierDto(updatedSupplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplierRepository.delete(supplier);
    }
}