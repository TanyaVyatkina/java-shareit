package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingRequestDto bookingDto) {
        User booker = findUserIfExists(userId);
        long itemId = bookingDto.getItemId();
        Item item = itemRepository.findByIdAndOwner_IdIsNot(bookingDto.getItemId(), userId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
        if (!item.getAvailable() || !validateDateOfBooking(itemId, bookingDto.getStart(), bookingDto.getEnd())) {
            throw new ValidationException("Данная вещь недоступна для бронирования.");
        }
        Booking savedBooking = bookingRepository.save(BookingMapper.toBooking(bookingDto, booker, item));
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto approveOrRejectBooking(long userId, long bookingId, boolean approve) {
        findUserIfExists(userId);
        Booking booking = findBookingIfExists(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ForbiddenException("Данная операция может быть выполнено только владельцем вещи.");
        }
        if (!BookingStatus.WAITING.equals(booking.getStatus())) {
            throw new ValidationException("Статус уже изменен.");
        }
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Некорректные даты бронирования.");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        findUserIfExists(userId);
        Booking booking = findBookingIfExists(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new ForbiddenException("Данная операция может быть выполнена либо автором бронирования, " +
                    "либо владельцем вещи, к которой относится бронирование");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, BookingState state, PageRequest page) {
        findUserIfExists(userId);
        List<Booking> foundBookings = null;
        switch (state) {
            case CURRENT:
                foundBookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case PAST:
                foundBookings = bookingRepository.findByBooker_IdAndEndBefore(userId, LocalDateTime.now(),
                        page);
                break;
            case FUTURE:
                foundBookings = bookingRepository.findByBooker_IdAndStartAfter(userId, LocalDateTime.now(),
                        page);
                break;
            case REJECTED:
                foundBookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, page);
                break;
            case WAITING:
                foundBookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, page);
                break;
            default:
                foundBookings = bookingRepository.findByBooker_Id(userId, page);
        }
        return BookingMapper.toBookingDtoList(foundBookings);
    }

    @Override
    public List<BookingDto> getBookingsForAllUserItems(long userId, BookingState state, PageRequest page) {
        findUserIfExists(userId);
        List<Item> userItems = itemRepository.findByOwner_Id(userId);
        if (userItems.isEmpty()) return Collections.emptyList();

        List<Long> itemIds = userItems
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> foundBookings = null;
        switch (state) {
            case CURRENT:
                foundBookings = bookingRepository.findByItem_IdInAndStartBeforeAndEndAfter(itemIds,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case PAST:
                foundBookings = bookingRepository.findByItem_IdInAndEndBefore(itemIds, LocalDateTime.now(), page);
                break;
            case FUTURE:
                foundBookings = bookingRepository.findByItem_IdInAndStartAfter(itemIds, LocalDateTime.now(), page);
                break;
            case REJECTED:
                foundBookings = bookingRepository.findByItem_IdInAndStatus(itemIds, BookingStatus.REJECTED, page);
                break;
            case WAITING:
                foundBookings = bookingRepository.findByItem_IdInAndStatus(itemIds, BookingStatus.WAITING, page);
                break;
            default:
                foundBookings = bookingRepository.findByItem_IdIn(itemIds, page);
        }
        return BookingMapper.toBookingDtoList(foundBookings);
    }

    private User findUserIfExists(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден."));
    }

    private Booking findBookingIfExists(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено."));
    }

    private boolean validateDateOfBooking(long itemId, LocalDateTime start, LocalDateTime end) {
        List<Booking> itemBookings = bookingRepository.findByItem_Id(itemId);
        for (Booking booking : itemBookings) {
            if (BookingStatus.APPROVED.equals(booking.getStatus())
                    && !(start.isAfter(booking.getEnd()) || end.isBefore(booking.getStart()))) {
                return false;
            }
        }
        return true;
    }
}
