package dev.tpcoder.goutbackend.common.exception;

public class RefreshTokenExpiredException extends RuntimeException  {

    public RefreshTokenExpiredException() {
        super();
    }

    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
