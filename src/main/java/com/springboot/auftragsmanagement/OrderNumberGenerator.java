package com.springboot.auftragsmanagement;

import java.util.concurrent.atomic.AtomicLong;

public final class OrderNumberGenerator {

    private static volatile OrderNumberGenerator instance;
    private final AtomicLong counter = new AtomicLong(0);

    private OrderNumberGenerator() {
        // Prevent direct instantiation
    }

    public static OrderNumberGenerator getInstance() {
        if (instance == null) {
            synchronized (OrderNumberGenerator.class) {
                if (instance == null) {
                    instance = new OrderNumberGenerator();
                }
            }
        }
        return instance;
    }

    public long generateNextOrderNumber() {
        return counter.incrementAndGet();
    }
}