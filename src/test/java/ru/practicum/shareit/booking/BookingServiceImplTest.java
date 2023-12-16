package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    private User user = createUser();
    private BookingRequestDto bookingRequestDto = new BookingRequestDto(1L,
            LocalDateTime.now(), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);
    private BookingService bookingService;

    @BeforeEach
    void initBookingService() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void testAddBooking_WithWrongUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException result = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingRequestDto));
        assertEquals(result.getMessage(), "Пользователь с id = 1 не найден.");
    }

    @Test
    void testAddBooking_WithWrongItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwner_IdIsNot(anyLong(), anyLong())).thenReturn(Optional.empty());
        NotFoundException result = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingRequestDto));
        assertEquals(result.getMessage(), "Вещь с id = 1 не найдена.");
    }

    @Test
    void testAddBooking_WithNotAvailableItem() {
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
    void testApproveOrRejectBooking_Approve() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Booking booking = createBooking(BookingStatus.WAITING);
        booking.setItem(createItem());
        booking.setBooker(new User());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        bookingService.approveOrRejectBooking(1L, 1L, true);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(booking);
    }

    @Test
    void testApproveOrRejectBooking_Reject() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Booking booking = createBooking(BookingStatus.WAITING);
        booking.setItem(createItem());
        booking.setBooker(new User());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        bookingService.approveOrRejectBooking(1L, 1L, false);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(booking);
    }

    @Test
    void testGetBooking_ForbiddenException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        ForbiddenException result = assertThrows(ForbiddenException.class,
                () -> bookingService.getBooking(111L, booking.getId()));
        assertEquals(result.getMessage(), "Данная операция может быть выполнена либо автором бронирования, " +
                "либо владельцем вещи, к которой относится бронирование");
    }

    @Test
    void testGetBooking_BookingFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.getBooking(booking.getBooker().getId(), booking.getId());
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void testGetBookingsForAllUserItems_BookingStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Item item = createItem();
        when(itemRepository.findByOwner_Id(anyLong())).thenReturn(List.of(item));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByItem_IdIn(any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getBookingsForAllUserItems(11L, BookingState.ALL,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetBookingsForAllUserItems_BookingStateCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Item item = createItem();
        when(itemRepository.findByOwner_Id(anyLong())).thenReturn(List.of(item));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByItem_IdInAndStartBeforeAndEndAfter(any(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getBookingsForAllUserItems(11L, BookingState.CURRENT,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetBookingsForAllUserItems_BookingStatePast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Item item = createItem();
        when(itemRepository.findByOwner_Id(anyLong())).thenReturn(List.of(item));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByItem_IdInAndEndBefore(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getBookingsForAllUserItems(11L, BookingState.PAST,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetBookingsForAllUserItems_BookingStateFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Item item = createItem();
        when(itemRepository.findByOwner_Id(anyLong())).thenReturn(List.of(item));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByItem_IdInAndStartAfter(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getBookingsForAllUserItems(11L, BookingState.FUTURE,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetBookingsForAllUserItems_BookingStateRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Item item = createItem();
        when(itemRepository.findByOwner_Id(anyLong())).thenReturn(List.of(item));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByItem_IdInAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getBookingsForAllUserItems(11L, BookingState.REJECTED,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetBookingsForAllUserItems_BookingStateWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Item item = createItem();
        when(itemRepository.findByOwner_Id(anyLong())).thenReturn(List.of(item));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByItem_IdInAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getBookingsForAllUserItems(11L, BookingState.WAITING,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetUserBookings_BookingStateCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getUserBookings(1L, BookingState.CURRENT,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetUserBookings_BookingStatePast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByBooker_IdAndEndBefore(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getUserBookings(1L, BookingState.PAST,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetUserBookings_BookingStateFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByBooker_IdAndStartAfter(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getUserBookings(1L, BookingState.FUTURE,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetUserBookings_BookingStateRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByBooker_IdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getUserBookings(1L, BookingState.REJECTED,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetUserBookings_BookingStateWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByBooker_IdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getUserBookings(1L, BookingState.WAITING,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
    }

    @Test
    void testGetUserBookings_BookingStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Booking booking = createBookingWithItemAndBooker();
        when(bookingRepository.findByBooker_Id(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> resultBookings = bookingService.getUserBookings(1L, BookingState.ALL,
                0, 3);
        assertEquals(1, resultBookings.size());
        assertEquals(booking.getId(), resultBookings.get(0).getId());
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

    private Booking createBookingWithItemAndBooker() {
        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(createItem());
        booking.setBooker(booker);

        return booking;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

}
