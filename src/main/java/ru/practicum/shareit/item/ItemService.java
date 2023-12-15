package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.List;

public interface ItemService {
    ItemShortDto addItem(long userId, ItemShortDto item);

    ItemShortDto updateItem(long userId, long itemId, ItemShortDto item);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> getUserItems(long userId, int from, int size);

    List<ItemShortDto> searchItems(long userId, String text, int from, int size);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
