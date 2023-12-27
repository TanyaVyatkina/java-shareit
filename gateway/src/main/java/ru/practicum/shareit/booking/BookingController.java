package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody @Valid BookingRequestDto bookingDto) {
        log.debug("Пришел новый запрос на бронирование вещи с id = {} от пользователя {}.",
                bookingDto.getItemId(), userId);
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new ValidationException("Неправильные даты бронирования.");
        }
        ResponseEntity<Object> savedBooking = bookingClient.bookItem(userId, bookingDto);
        log.debug("Бронирование добавлено.");
        return (BookingDto) savedBooking.getBody();
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("bookingId") long bookingId, @RequestParam boolean approved) {
        log.debug("Запрос на подтверждение или отклонение бронирования (id = {}), " +
                "(approve = {}), от пользователя id = {}.", bookingId, approved, userId);
        BookingDto bookingDto = (BookingDto) bookingClient.approveOrReject(userId, bookingId, approved).getBody();
        log.debug("Статус бронирования изменен.");
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable("bookingId") long bookingId) {
        log.debug("Запрос на получение данных о бронировании (id = {}) " +
                "от пользователя (id = {}).", bookingId, userId);
        BookingDto bookingDto = (BookingDto) bookingClient.getBooking(userId, bookingId).getBody();
        log.debug("Найдено бронирование: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") @Min(0) int from,
                                            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Получение списка всех бронирований пользователя (id = {}).", userId);
        BookingState stateEnum = BookingState.toEnum(state);
        ResponseEntity<Object> foundBookings = bookingClient.getBookings("", userId, stateEnum, from, size);
        log.debug("Найдены бронирования: {}.", foundBookings);
        return (List<BookingDto>) foundBookings.getBody();
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Получение списка бронирований для всех вещей пользователя (id = {}).", userId);
        BookingState stateEnum = BookingState.toEnum(state);
        ResponseEntity<Object> foundBookings = bookingClient.getBookings("/owner", userId, stateEnum, from, size);
        log.debug("Найдены бронирования: {}.", foundBookings);
        return (List<BookingDto>) foundBookings;
    }
}
