package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
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
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto addBooking(Long userId, BookingRequestDto bookingDto) {
        validateDatesOfBooking(bookingDto.getStart(), bookingDto.getEnd());
        User booker = findUserIfExists(userId);
        Item item = findItemIfExists(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Данная вещь недоступна для бронирования.");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Бронируемая вещь не должна Вам принадлежать.");
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
    public List<BookingDto> getUserBookings(long userId, BookingState state) {
        findUserIfExists(userId);
        List<Booking> foundBookings = null;
        switch (state) {
            case CURRENT:
                foundBookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByIdAsc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                foundBookings = bookingRepository.findByBooker_IdAndEndBeforeOrderByIdDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                foundBookings = bookingRepository.findByBooker_IdAndStartAfterOrderByIdDesc(userId, LocalDateTime.now());
                break;
            case REJECTED:
                foundBookings = bookingRepository.findByBooker_IdAndStatusOrderByIdDesc(userId, BookingStatus.REJECTED);
                break;
            case WAITING:
                foundBookings = bookingRepository.findByBooker_IdAndStatusOrderByIdDesc(userId, BookingStatus.WAITING);
                break;
            default:
                foundBookings = bookingRepository.findByBooker_IdOrderByIdDesc(userId);
        }
        return BookingMapper.toBookingDtoList(foundBookings);
    }

    @Override
    public List<BookingDto> getBookingsForAllUserItems(long userId, BookingState state) {
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
                foundBookings = bookingRepository.findByItem_IdInAndStartBeforeAndEndAfterOrderByIdDesc(itemIds,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                foundBookings = bookingRepository.findByItem_IdInAndEndBeforeOrderByIdDesc(itemIds, LocalDateTime.now());
                break;
            case FUTURE:
                foundBookings = bookingRepository.findByItem_IdInAndStartAfterOrderByIdDesc(itemIds, LocalDateTime.now());
                break;
            case REJECTED:
                foundBookings = bookingRepository.findByItem_IdInAndStatusOrderByIdDesc(itemIds, BookingStatus.REJECTED);
                break;
            case WAITING:
                foundBookings = bookingRepository.findByItem_IdInAndStatusOrderByIdDesc(itemIds, BookingStatus.WAITING);
                break;
            default:
                foundBookings = bookingRepository.findByItem_IdInOrderByIdDesc(itemIds);
        }
        return BookingMapper.toBookingDtoList(foundBookings);
    }

    private User findUserIfExists(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден."));
    }

    private Item findItemIfExists(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найденa."));
    }

    private Booking findBookingIfExists(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено."));
    }

    private void validateDatesOfBooking(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(start) || end.isEqual(start)) {
            throw new ValidationException("Неправильные даты бронирования.");
        }
    }
}
