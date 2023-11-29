package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.OnUpdate;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody @Valid BookingRequestDto bookingDto) {
        log.debug("Пришел новый запрос на бронирование вещи с id = {} от пользователя {}.",
                bookingDto.getItemId(), userId);
        bookingDto.setStatus(BookingStatus.WAITING);
        BookingDto savedBooking = bookingService.addBooking(userId, bookingDto);
        log.debug("Бронирование добавлено.");
        return savedBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable("bookingId") long bookingId, @RequestParam boolean approve) {
        log.debug("Запрос на подтверждение или отклонение бронирования (id = {}), " +
                "(approve = {}), от пользователя id = {}.", bookingId, approve, userId);
        BookingDto bookingDto = bookingService.approveOrRejectBooking(userId, bookingId, approve);
        log.debug("Статус бронирования изменен.");
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable("bookingId") long bookingId) {
        log.debug("Запрос на получение данных о бронировании (id = {}) " +
                "от пользователя (id = {}).", bookingId, userId);
        BookingDto bookingDto = bookingService.getBooking(userId, bookingId);
        log.debug("Найдено бронирование: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.debug("получение списка всех бронирований пользователя (id = {}).", userId);
        List<BookingDto> foundBookings = bookingService.getUserBookings(userId , state);
        log.debug("Найдены бронирования: {}.", foundBookings);
        BookingState.ALL.name();
        return foundBookings;
    }

    //Еще 1 метод
}
