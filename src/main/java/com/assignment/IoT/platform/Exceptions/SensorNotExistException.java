package com.assignment.IoT.platform.Exceptions;

public class SensorNotExistException extends RuntimeException {
    public SensorNotExistException() {
        super();
    }

    public SensorNotExistException(String message) {
        super(message);
    }
}