package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingRequestDto bookingDto);

    BookingDto approveOrRejectBooking(long userId, long bookingId, boolean approve);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getUserBookings(long userId, BookingState state, PageRequest page);

    List<BookingDto> getBookingsForAllUserItems(long userId, BookingState state, PageRequest page);

}
