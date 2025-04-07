package com.digitalfutures.academy.spring_demo.exceptions;

public class WeightLogEntryEmptyException extends RuntimeException {
    public WeightLogEntryEmptyException(String message) {
        super(message);
    }
}