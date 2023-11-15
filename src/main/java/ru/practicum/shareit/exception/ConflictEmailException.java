package ru.practicum.shareit.exception;

public class ConflictEmailException extends RuntimeException {
    public ConflictEmailException(String message) {
        super(message);
    }

}
