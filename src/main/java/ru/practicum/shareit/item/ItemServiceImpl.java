package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    private ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemShortDto addItem(long userId, ItemShortDto itemDto) {
        User user = findUserIfExists(userId);
        Long requestId = itemDto.getRequestId();
        ItemRequest itemRequest = null;
        if (requestId != null) {
            itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден."));
        }
        Item item = ItemMapper.toItem(itemDto, user, itemRequest);
        item.setOwner(user);
        item = itemRepository.save(item);
        return ItemMapper.toItemShortDto(item);
    }

    @Override
    public ItemShortDto updateItem(long userId, long itemId, ItemShortDto itemDto) {
        findUserIfExists(userId);
        Item item = itemRepository.findByIdAndOwner_Id(itemId, userId)
                .orElseThrow(() -> new NotFoundException("Данная вещь для указанного пользователя не найдена."));
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
        return ItemMapper.toItemShortDto(item);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        findUserIfExists(userId);
        Item item = findItemIfExists(itemId);
        List<Comment> comments = commentRepository.findByItem_Id(item.getId());
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookingsForItem = bookingRepository.findByItem_Id(item.getId());
            return ItemMapper.toItemDto(item, CommentMapper.toCommentDtoList(comments), bookingsForItem);
        }
        return ItemMapper.toItemDto(item, CommentMapper.toCommentDtoList(comments), null);
    }

    @Override
    public List<ItemDto> getUserItems(long userId, int from, int size) {
        findUserIfExists(userId);
        PageRequest page = PageRequest.of(from / size, size);
        List<Item> foundItems = itemRepository.findByOwner_Id(userId, page);
        List<Long> itemIds = foundItems.stream()
                .map(Item::getId)
                .collect(toList());

        List<Comment> comments = commentRepository.findByItem_IdIn(itemIds);
        Map<Item, List<Comment>> commentsMap = comments
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        List<Booking> bookings = bookingRepository.findByItem_IdIn(itemIds);
        Map<Item, List<Booking>> bookingsMap = bookings
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        List<ItemDto> foundItemsDto = new ArrayList<>();
        for (Item item : foundItems) {
            List<Comment> itemComments = commentsMap.get(item);
            List<Booking> itemBookings = bookingsMap.get(item);
            foundItemsDto.add(ItemMapper.toItemDto(item, CommentMapper.toCommentDtoList(itemComments), itemBookings));
        }
        return foundItemsDto;
    }

    @Override
    public List<ItemShortDto> searchItems(long userId, String text, int from, int size) {
        findUserIfExists(userId);
        if (text.isBlank()) return new ArrayList<>();
        PageRequest page = PageRequest.of(from / size, size);
        List<Item> foundItems = itemRepository.findItemsByText(text, page);
        return ItemMapper.toItemShortDtoList(foundItems);
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = findUserIfExists(userId);
        Item item = findItemIfExists(itemId);
        List<Booking> itemBookings = bookingRepository.findByItem_IdAndBooker_IdAndEndBefore(itemId, userId,
                LocalDateTime.now());
        if (itemBookings.isEmpty()) {
            throw new ValidationException("Отзыв может оставить только тот пользователь, " +
                    "который брал эту вещь в аренду, и только после окончания срока аренды.");
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
}
