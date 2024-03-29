package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

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
                                 @RequestBody BookingRequestDto bookingDto) {
        log.debug("Пришел новый запрос на бронирование вещи с id = {} от пользователя {}.",
                bookingDto.getItemId(), userId);
        BookingDto savedBooking = bookingService.addBooking(userId, bookingDto);
        log.debug("Бронирование добавлено.");
        return savedBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("bookingId") long bookingId, @RequestParam boolean approved) {
        log.debug("Запрос на подтверждение или отклонение бронирования (id = {}), " +
                "(approve = {}), от пользователя id = {}.", bookingId, approved, userId);
        BookingDto bookingDto = bookingService.approveOrRejectBooking(userId, bookingId, approved);
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
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.debug("Получение списка всех бронирований пользователя (id = {}).", userId);
        BookingState stateEnum = BookingState.toEnum(state);
        PageRequest page = PageRequest.of(from / size, size).withSort(Sort.Direction.DESC, "start");
        List<BookingDto> foundBookings = bookingService.getUserBookings(userId, stateEnum, page);
        log.debug("Найдены бронирования: {}.", foundBookings);
        return foundBookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        log.debug("Получение списка бронирований для всех вещей пользователя (id = {}).", userId);
        BookingState stateEnum = BookingState.toEnum(state);
        PageRequest page = PageRequest.of(from / size, size).withSort(Sort.Direction.DESC, "start");
        List<BookingDto> foundBookings = bookingService.getBookingsForAllUserItems(userId, stateEnum, page);
        log.debug("Найдены бронирования: {}.", foundBookings);
        return foundBookings;
    }
}
