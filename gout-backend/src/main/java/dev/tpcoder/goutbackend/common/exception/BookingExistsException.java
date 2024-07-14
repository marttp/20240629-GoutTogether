package dev.tpcoder.goutbackend.common.exception;

public class BookingExistsException extends RuntimeException {
    
    public BookingExistsException() {
        super();
    }

    public BookingExistsException(String message) {
        super(message);
    }
}
