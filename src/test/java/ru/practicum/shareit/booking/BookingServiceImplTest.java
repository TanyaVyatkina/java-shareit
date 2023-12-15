package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    User user = createUser();
    BookingRequestDto bookingRequestDto = new BookingRequestDto(1L,
            LocalDateTime.now(), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

    @Test
    void testAddBooking_WithWrongUser() {
        BookingService bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException result = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingRequestDto));
        assertEquals(result.getMessage(), "Пользователь с id = 1 не найден.");
    }

    @Test
    void testAddBooking_WithWrongItem() {
        BookingService bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwner_IdIsNot(anyLong(), anyLong())).thenReturn(Optional.empty());
        NotFoundException result = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingRequestDto));
        assertEquals(result.getMessage(), "Вещь с id = 1 не найдена.");
    }

    @Test
    void testAddBooking_WithNotAvailableItem() {
        BookingService bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Item item = createItem();
        item.setAvailable(false);
        when(itemRepository.findByIdAndOwner_IdIsNot(anyLong(), anyLong())).thenReturn(Optional.of(item));
        ValidationException result = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(1L, bookingRequestDto));
        assertEquals(result.getMessage(), "Данная вещь недоступна для бронирования.");
    }

    @Test
    void testAddBooking_WithWrongTime() {
        BookingService bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwner_IdIsNot(anyLong(), anyLong())).thenReturn(Optional.of(createItem()));

        Booking booking = createBooking(bookingRequestDto.getStart(), bookingRequestDto.getEnd(), BookingStatus.APPROVED);
        when(bookingRepository.findByItem_Id(anyLong())).thenReturn(List.of(booking));
        ValidationException result = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(1L, bookingRequestDto));
        assertEquals(result.getMessage(), "Данная вещь недоступна для бронирования.");
    }

    @Test
    void testApproveOrRejectBooking_ByWrongUser() {
        BookingService bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Booking booking = createBooking(BookingStatus.WAITING);
        booking.setItem(createItem());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ForbiddenException result = assertThrows(ForbiddenException.class,
                () -> bookingService.approveOrRejectBooking(111L, 1L, true));
        assertEquals(result.getMessage(), "Данная операция может быть выполнено только владельцем вещи.");
    }

    @Test
    void testApproveOrRejectBooking_WithWrongStatus() {
        BookingService bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Booking booking = createBooking(BookingStatus.APPROVED);
        booking.setItem(createItem());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ValidationException result = assertThrows(ValidationException.class,
                () -> bookingService.approveOrRejectBooking(1L, 1L, true));
        assertEquals(result.getMessage(), "Статус уже изменен.");
    }

    @Test
    void testApproveOrRejectBooking_WithWrongTime() {
        BookingService bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Booking booking = createBooking(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1),
                BookingStatus.WAITING);
        booking.setItem(createItem());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ValidationException result = assertThrows(ValidationException.class,
                () -> bookingService.approveOrRejectBooking(1L, 1L, true));
        assertEquals(result.getMessage(), "Некорректные даты бронирования.");
    }

    @Test
    void testApproveOrRejectBooking_AllOk() {
        BookingService bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Booking booking = createBooking(BookingStatus.WAITING);
        booking.setItem(createItem());
        booking.setBooker(new User());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        bookingService.approveOrRejectBooking(1L, 1L, true);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(booking);
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        return item;
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return booking;
    }

    private Booking createBooking(BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(status);
        return booking;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

}
