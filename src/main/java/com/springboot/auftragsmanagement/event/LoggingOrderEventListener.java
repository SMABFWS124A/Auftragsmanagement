package com.springboot.auftragsmanagement.event;

import com.springboot.auftragsmanagement.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observer that logs all order events to provide visibility into the domain flow.
 */
@Component
public class LoggingOrderEventListener implements OrderEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingOrderEventListener.class);

    @Override
    public void onOrderCreated(Order order) {
        LOGGER.info("Order {} created for customer {}", order.getId(), order.getCustomer().getId());
    }

    @Override
    public void onOrderDelivered(Order order) {
        LOGGER.info("Order {} delivered", order.getId());
    }
}