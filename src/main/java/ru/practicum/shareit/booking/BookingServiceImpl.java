package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

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
        User booker = findUserIfExists(userId);
        Item item = findItemIfExists(bookingDto.getItemId());
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
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long userId, long bookingId) {
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
        List<Booking> foundBookings = bookingRepository.findAll();
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
}
