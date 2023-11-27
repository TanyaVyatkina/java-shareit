package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(int userId, ItemDto item);

    ItemDto updateItem(int userId, int itemId, ItemDto item);

    ItemDto getItem(int userId, int itemId);

    List<ItemDto> getUserItems(int userId);

    List<ItemDto> searchItems(int userId, String text);
}
