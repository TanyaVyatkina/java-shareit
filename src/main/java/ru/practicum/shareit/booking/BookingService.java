package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingRequestDto bookingDto);
    BookingDto approveOrRejectBooking(long userId, long bookingId, boolean approve);
    BookingDto getBooking(Long userId, long bookingId);
    List<BookingDto> getUserBookings(long userId, BookingState state);
}
