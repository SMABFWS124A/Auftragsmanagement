package com.springboot.auftragsmanagement.exception;
public class StockExceededException extends RuntimeException {

    public StockExceededException(String message) {
        super(message);
    }

    public StockExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}