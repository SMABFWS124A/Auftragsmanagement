package com.springboot.auftragsmanagement.service.decorator;

import com.springboot.auftragsmanagement.dto.OrderDto;
import com.springboot.auftragsmanagement.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Decorator Pattern: fügt dem bestehenden {@link OrderService} einfache Audit-Logs hinzu,
 * ohne dessen Implementierung anzupassen.
 */
@Service
@Primary
public class LoggingOrderServiceDecorator implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(LoggingOrderServiceDecorator.class);

    private final OrderService delegate;

    public LoggingOrderServiceDecorator(@Qualifier("orderServiceImpl") OrderService delegate) {
        this.delegate = delegate;
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        log.info("[ORDER] Erstelle Auftrag für Kunde {} mit {} Positionen", orderDto.customerId(), orderDto.items().size());
        OrderDto created = delegate.createOrder(orderDto);
        log.info("[ORDER] Auftrag {} erstellt mit Status {} und Summe {}", created.id(), created.status(), created.totalAmount());
        return created;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        log.debug("[ORDER] Lade alle Aufträge");
        return delegate.getAllOrders();
    }

    @Override
    public OrderDto deliverOrder(Long orderId) {
        log.info("[ORDER] Liefere Auftrag {} aus", orderId);
        OrderDto delivered = delegate.deliverOrder(orderId);
        log.info("[ORDER] Auftrag {} erfolgreich ausgeliefert", delivered.id());
        return delivered;
    }

    @Override
    public void deleteDeliveredOrder(Long orderId) {
        log.info("[ORDER] Lösche ausgelieferten Auftrag {}", orderId);
        delegate.deleteDeliveredOrder(orderId);
    }
}