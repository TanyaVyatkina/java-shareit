package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.List;

public interface ItemService {
    ItemShortDto addItem(long userId, ItemShortDto item);

    ItemShortDto updateItem(long userId, long itemId, ItemShortDto item);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> getUserItems(long userId);

    List<ItemShortDto> searchItems(long userId, String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
