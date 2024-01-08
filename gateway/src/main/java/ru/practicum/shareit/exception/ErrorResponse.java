package ru.practicum.shareit.exception;

public class ErrorResponse {
    private final String error;
    private final String stack;

    public ErrorResponse(String error, String stack) {
        this.error = error;
        this.stack = stack;
    }

    public ErrorResponse(String error) {
        this.error = error;
        this.stack = null;
    }

    public String getError() {
        return error;
    }

    public String getStack() {
        return stack;
    }
}
