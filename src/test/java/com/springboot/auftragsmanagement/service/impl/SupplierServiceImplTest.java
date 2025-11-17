package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.SupplierDto;
import com.springboot.auftragsmanagement.entity.Supplier;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private SupplierDto supplierDto;
    private Supplier supplierEntity;

    private final Long TEST_ID = 1L;
    private final String TEST_NAME = "Test Supplier GmbH";
    private final String TEST_EMAIL = "info@test.de";
    private final String TEST_CONTACT = "Max Mustermann";
    private final String TEST_ADDRESS = "Musterstr. 1";
    private final String TEST_PHONE = "0123456789";

    @BeforeEach
    void setUp() {
        // Erstellen der Test-Entity
        supplierEntity = new Supplier();
        supplierEntity.setId(TEST_ID);
        supplierEntity.setName(TEST_NAME);
        supplierEntity.setEmail(TEST_EMAIL);
        supplierEntity.setContactPerson(TEST_CONTACT);
        supplierEntity.setAddress(TEST_ADDRESS);
        supplierEntity.setPhone(TEST_PHONE);

        // Erstellen des Test-DTOs
        supplierDto = new SupplierDto();
        supplierDto.setId(TEST_ID);
        supplierDto.setName(TEST_NAME);
        supplierDto.setEmail(TEST_EMAIL);
        supplierDto.setContactPerson(TEST_CONTACT);
        supplierDto.setAddress(TEST_ADDRESS);
        supplierDto.setPhone(TEST_PHONE);
    }

    /**
     * Helferfunktion zur Erstellung eines Input-DTO ohne ID.
     */
    private SupplierDto createInputDto() {
        SupplierDto input = new SupplierDto();
        input.setName(TEST_NAME);
        input.setEmail(TEST_EMAIL);
        input.setContactPerson(TEST_CONTACT);
        input.setAddress(TEST_ADDRESS);
        input.setPhone(TEST_PHONE);
        return input;
    }

    // ===================================
    // 1. Tests für createSupplier
    // ===================================

    @Test
    void createSupplier_ShouldSaveAndReturnDtoWithId() {
        // Arrange
        SupplierDto inputDto = createInputDto();

        // Simuliert, dass das Repository die Entity mit generierter ID zurückgibt
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplierEntity);

        // Act
        SupplierDto result = supplierService.createSupplier(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_NAME, result.getName());
        verify(supplierRepository).save(any(Supplier.class));
    }

    // ===================================
    // 2. Tests für getSupplierById
    // ===================================

    @Test
    void getSupplierById_ShouldReturnDto_WhenFound() {
        // Arrange
        when(supplierRepository.findById(TEST_ID)).thenReturn(Optional.of(supplierEntity));

        // Act
        SupplierDto result = supplierService.getSupplierById(TEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_EMAIL, result.getEmail());
        verify(supplierRepository).findById(TEST_ID);
    }

    @Test
    void getSupplierById_ShouldThrowException_WhenNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(supplierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierById(nonExistentId));
        verify(supplierRepository).findById(nonExistentId);
    }

    // ===================================
    // 3. Tests für getAllSuppliers
    // ===================================

    @Test
    void getAllSuppliers_ShouldReturnListOfDtos() {
        // Arrange
        Supplier supplier2 = new Supplier();
        supplier2.setId(2L);
        supplier2.setName("Second Supplier");

        List<Supplier> supplierList = Arrays.asList(supplierEntity, supplier2);
        when(supplierRepository.findAll()).thenReturn(supplierList);

        // Act
        List<SupplierDto> result = supplierService.getAllSuppliers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TEST_ID, result.get(0).getId());
        assertEquals("Second Supplier", result.get(1).getName());
        verify(supplierRepository).findAll();
    }

    // ===================================
    // 4. Tests für updateSupplier
    // ===================================

    @Test
    void updateSupplier_ShouldUpdateFieldsAndReturnUpdatedDto() {
        // Arrange
        String newName = "Updated Name";
        SupplierDto updateDto = supplierDto;
        updateDto.setName(newName);

        // Setup: FindById gibt die existierende Entity zurück
        when(supplierRepository.findById(TEST_ID)).thenReturn(Optional.of(supplierEntity));
        // Setup: Save gibt dieselbe, nun aktualisierte Entity zurück
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplierEntity);

        // Act
        SupplierDto result = supplierService.updateSupplier(TEST_ID, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(newName, result.getName());
        // Verifiziere, dass die Entity im Mock-Objekt aktualisiert wurde, bevor sie gespeichert wurde
        verify(supplierRepository).findById(TEST_ID);
        verify(supplierRepository).save(supplierEntity);
    }

    @Test
    void updateSupplier_ShouldThrowException_WhenNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(supplierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> supplierService.updateSupplier(nonExistentId, supplierDto));
        verify(supplierRepository, never()).save(any());
    }

    // ===================================
    // 5. Tests für deleteSupplier
    // ===================================

    @Test
    void deleteSupplier_ShouldCallRepositoryDelete() {
        // Arrange
        when(supplierRepository.findById(TEST_ID)).thenReturn(Optional.of(supplierEntity));
        doNothing().when(supplierRepository).delete(supplierEntity);

        // Act
        supplierService.deleteSupplier(TEST_ID);

        // Assert
        verify(supplierRepository).findById(TEST_ID);
        verify(supplierRepository).delete(supplierEntity);
    }

    @Test
    void deleteSupplier_ShouldThrowException_WhenNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(supplierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> supplierService.deleteSupplier(nonExistentId));
        verify(supplierRepository, never()).delete(any());
    }
}