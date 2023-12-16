package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private User user = createUser();
    private ItemShortDto itemShortDto = new ItemShortDto(1L, "Барабан",
            "Отлично играет", true, 1L, null);

    @BeforeEach
    void initItemService() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository);
    }

    @Test
    void testAddItem_WithWrongUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException result = assertThrows(NotFoundException.class,
                () -> itemService.addItem(1L, itemShortDto));
        assertEquals(result.getMessage(), "Пользователь с id = 1 не найден.");
    }

    @Test
    void testAddItem_WithWrongItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        itemShortDto.setRequestId(1111L);
        NotFoundException result = assertThrows(NotFoundException.class,
                () -> itemService.addItem(1L, itemShortDto));
        assertEquals(result.getMessage(), "Запрос с id = 1111 не найден.");
    }

    @Test
    void testAddItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        ItemShortDto dto = new ItemShortDto(null, "Лопата",
                "Не ржавая", true, 1L, null);
        when(itemRepository.save(any())).thenReturn(createItemWithUser());
        itemService.addItem(1L, itemShortDto);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void testGetItem_WithComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item item = createItemWithUser();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setId(1L);
        comment.setText("Text");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        when(commentRepository.findByItem_Id(anyLong())).thenReturn(List.of(comment));

        ItemDto resultItem = itemService.getItem(user.getId(), item.getId());

        assertEquals(resultItem.getId(), item.getId());
        assertEquals(resultItem.getName(), item.getName());
        assertEquals(resultItem.getDescription(), item.getDescription());

        assertEquals(resultItem.getComments().size(), 1);
        assertEquals(resultItem.getComments().get(0).getId(), comment.getId());
        assertEquals(resultItem.getComments().get(0).getText(), comment.getText());
    }

    @Test
    void testGetItem_ByOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item item = createItemWithUser();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(commentRepository.findByItem_Id(anyLong())).thenReturn(null);

        User booker = new User();
        booker.setId(2L);

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setBooker(booker);

        Booking booking2 = new Booking();
        booking2.setId(1L);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusDays(3));
        booking2.setEnd(LocalDateTime.now().minusDays(2));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(booker);

        when(bookingRepository.findByItem_Id(anyLong())).thenReturn(List.of(booking1, booking2));

        ItemDto resultItem = itemService.getItem(user.getId(), item.getId());

        assertEquals(resultItem.getId(), item.getId());
        assertEquals(resultItem.getName(), item.getName());
        assertEquals(resultItem.getDescription(), item.getDescription());

        assertNotNull(resultItem.getLastBooking());
        assertNotNull(resultItem.getNextBooking());
        assertEquals(resultItem.getLastBooking().getId(), booking2.getId());
        assertEquals(resultItem.getNextBooking().getId(), booking1.getId());
    }

    @Test
    void testGetItem_ByNotOwner() {
        User newUser = new User();
        newUser.setId(111L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));

        Item item = createItemWithUser();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);

        ItemDto resultItem = itemService.getItem(newUser.getId(), item.getId());
        assertEquals(resultItem.getId(), item.getId());
        assertEquals(resultItem.getName(), item.getName());
        assertEquals(resultItem.getDescription(), item.getDescription());
        assertNull(resultItem.getLastBooking());
        assertNull(resultItem.getNextBooking());
    }

    @Test
    void testAddComment_WithoutBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createItemWithUser()));
        when(bookingRepository.findByItem_IdAndBooker_IdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(new ArrayList<>());
        ValidationException result = assertThrows(ValidationException.class,
                () -> itemService.addComment(1L, 1L, new CommentDto(null, "Text",
                        "Name", LocalDateTime.now())));
        assertEquals(result.getMessage(), "Отзыв может оставить только тот пользователь, " +
                "который брал эту вещь в аренду, и только после окончания срока аренды.");
    }

    @Test
    void testUpdateItem_ItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        when(itemRepository.findByIdAndOwner_Id(anyLong(), anyLong())).thenReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, itemShortDto));
        assertEquals(result.getMessage(), "Данная вещь для указанного пользователя не найдена.");
    }

    @Test
    void testUpdateItem_ItemUpdated() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        Item item = createItemWithUser();
        when(itemRepository.findByIdAndOwner_Id(anyLong(), anyLong())).thenReturn(Optional.of(item));
        itemService.updateItem(1L, 1L, itemShortDto);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testSearchItems_TextIsBlank() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        List<ItemShortDto> resultItems = itemService.searchItems(1L, "", 0, 1);
        assertTrue(resultItems.isEmpty());
    }

    @Test
    void testSearchItems_FoundItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        Item item = createItemWithUser();
        when(itemRepository.findItemsByText(anyString(), any())).thenReturn(List.of(item));
        List<ItemShortDto> resultItems = itemService.searchItems(1L, "text", 0, 1);
        assertEquals(1, resultItems.size());
        assertEquals(item.getId(), resultItems.get(0).getId());
    }

    private Item createItemWithUser() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        return item;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }
}
