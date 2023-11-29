package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, long itemId, ItemDto item);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> getUserItems(long userId);

    List<ItemDto> searchItems(long userId, String text);
}
