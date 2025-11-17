package com.springboot.auftragsmanagement.controller;

import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.exception.StockExceededException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest; // Neu importiert
import static org.mockito.Mockito.mock; // Neu importiert
import static org.mockito.Mockito.when;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final WebRequest mockWebRequest = mock(WebRequest.class);

    // Initialisiere den Mock, da alle Handler jetzt WebRequest benötigen
    public GlobalExceptionHandlerTest() {
        // Mock-Verhalten für die Anforderungsdetails
        when(mockWebRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "id", 1L);
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleResourceNotFoundException(ex, mockWebRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found with id : '1'", response.getBody().get("message"));
    }

    @Test
    void handleStockExceededException_ShouldReturnConflict() {
        StockExceededException ex = new StockExceededException("Stock exceeded");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleStockExceededException(ex, mockWebRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Stock exceeded", response.getBody().get("message"));
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgumentException(ex, mockWebRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid argument", response.getBody().get("message"));
    }

    @Test
    void handleIllegalStateException_ShouldReturnUnprocessableEntity() {
        IllegalStateException ex = new IllegalStateException("Invalid state");
        // Erwartet jetzt 422, da es der im Handler definierte Status ist
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalStateException(ex, mockWebRequest);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid state", response.getBody().get("message"));
    }

    @Test
    void handleDataIntegrityViolationException_ShouldReturnBadRequest() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "Cannot add or update a child row: a foreign key constraint fails"
        );
        // Erwartet jetzt 400, da es der im Handler definierte Status ist
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDataIntegrityViolationException(ex, mockWebRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        // Überprüft die generische Fehlermeldung, die der Handler ausgibt
        assertEquals("Datenfehler: Die übermittelten Daten verletzen Datenbankregeln (z.B. ungültiger Fremdschlüssel).", response.getBody().get("message"));
    }

    @Test
    void handleGeneralException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("General error");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneralException(ex, mockWebRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        // Überprüft die generische Fehlermeldung
        assertEquals("Ein interner Serverfehler ist aufgetreten. Bitte überprüfen Sie die Server-Logs.", response.getBody().get("message"));
    }
}