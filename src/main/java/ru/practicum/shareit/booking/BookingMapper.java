package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getStatus());
        dto.setItem(ItemMapper.toItemForBookingDto(booking.getItem()));
        dto.setBooker(UserMapper.toUserForBookingDto(booking.getBooker()));
        return dto;
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        BookingShortDto dto = new BookingShortDto(booking.getId(), booking.getItem().getId(),
                booking.getBooker().getId(), booking.getStart(), booking.getEnd());
        return dto;
    }

    public static List<BookingDto> toBookingDtoList(List<Booking> bookings) {
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public static Booking toBooking(BookingRequestDto dto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(dto.getStatus());
        booking.setItem(item);
        return booking;
    }
}
