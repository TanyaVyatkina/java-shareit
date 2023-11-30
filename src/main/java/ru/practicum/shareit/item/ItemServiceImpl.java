package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User user = findUserIfExists(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        item.setOwner(user);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        User user = findUserIfExists(userId);
        Item item = findItemIfExists(itemId);
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new NotFoundException("Данная вещь для указанного пользователя не найдена.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        findUserIfExists(userId);
        Item item = findItemIfExists(itemId);
        if (item.getOwner().getId().equals(userId)) {
            return createItemDtoWithBookings(item);
        }
        return createItemDtoWithoutBookings(item);
    }

    @Override
    public List<ItemDto> getUserItems(long userId) {
        findUserIfExists(userId);
        List<Item> foundItems = itemRepository.findByOwner_Id(userId);
        List<ItemDto> foundItemsDto = new ArrayList<>();

        for (Item item : foundItems) {
            ItemDto dto = createItemDtoWithBookings(item);
            foundItemsDto.add(dto);
        }
        return foundItemsDto;
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        findUserIfExists(userId);
        if (text.isBlank()) return new ArrayList<>();
        List<Item> foundItems = itemRepository.findItemsByText(text);
        return ItemMapper.toItemDtoList(foundItems);
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = findUserIfExists(userId);
        Item item = findItemIfExists(itemId);
        List<Booking> itemBookings = bookingRepository.findByItem_IdAndBooker_IdAndEndBefore(itemId, userId,
                LocalDateTime.now());
        if (itemBookings.isEmpty()) {
            throw new ValidationException("Отзыв может оставить только тот пользователь, " +
                    "который брал эту вещь в аренду, и только после окончания срока аренды");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));
        return CommentMapper.toCommentDto(comment);
    }

    private User findUserIfExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
    }

    private Item findItemIfExists(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найденa."));
    }

    private ItemDto createItemDtoWithoutBookings(Item item) {
        ItemDto dto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findByItem_Id(item.getId());
        dto.setComments(CommentMapper.toCommentDtoList(comments));
        return dto;
    }

    private ItemDto createItemDtoWithBookings(Item item) {
        ItemDto dto = createItemDtoWithoutBookings(item);
        List<Booking> bookingsForItem = bookingRepository.findByItem_Id(item.getId());
        Booking nextBooking = bookingsForItem
                .stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()) && !BookingStatus.REJECTED.equals(b.getStatus()))
                .sorted((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                .findFirst()
                .orElse(null);
        Booking lastBooking = bookingsForItem
                .stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()) && !BookingStatus.REJECTED.equals(b.getStatus()))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .findFirst()
                .orElse(null);
        dto.setLastBooking(lastBooking != null ? BookingMapper.toBookingShortDto(lastBooking) : null);
        dto.setNextBooking(nextBooking != null ? BookingMapper.toBookingShortDto(nextBooking) : null);
        return dto;
    }
}
