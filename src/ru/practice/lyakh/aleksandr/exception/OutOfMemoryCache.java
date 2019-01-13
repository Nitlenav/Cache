package ru.practice.lyakh.aleksandr.exception;

public class OutOfMemoryCache extends RuntimeException {
    public OutOfMemoryCache() {
    }

    public OutOfMemoryCache(String message) {
        super(message);
    }

    public OutOfMemoryCache(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfMemoryCache(Throwable cause) {
        super(cause);
    }
}
