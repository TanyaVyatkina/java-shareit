package ru.practicum.shareit.booking.dto;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState toEnum(String str) {
        BookingState state;
        try {
            state = BookingState.valueOf(str);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown state: " + str);
        }
        return state;
    }
}
