package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item add(Item item);

    Optional<Item> findById(int id);

    void update(Item item);

    List<Item> getItemsByUserId(int userId);

    List<Item> search(String text);
}
