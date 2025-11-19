package com.springboot.auftragsmanagement.event;

import com.springboot.auftragsmanagement.entity.Order;

/**
 * Observer interface for reacting to order life-cycle events.
 */
public interface OrderEventListener {

    default void onOrderCreated(Order order) {
    }

    default void onOrderDelivered(Order order) {
    }
}