package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.List;

public interface ItemService {
    ItemShortDto addItem(long userId, ItemShortDto item);

    ItemShortDto updateItem(long userId, long itemId, ItemShortDto item);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> getUserItems(long userId, PageRequest page);

    List<ItemShortDto> searchItems(long userId, String text, PageRequest page);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
