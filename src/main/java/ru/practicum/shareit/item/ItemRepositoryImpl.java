package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private int itemId = 0;
    private Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        int newId = getItemId();
        item.setId(newId);
        items.put(newId, item);
        return item;
    }

    @Override
    public Optional<Item> findById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public List<Item> getItemsByUserId(int userId) {
        return items.values()
                .stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return items.values()
                .stream()
                .filter(i -> isItemAvailiable(i, text))
                .collect(Collectors.toList());
    }

    private int getItemId() {
        return ++itemId;
    }

    private boolean isItemAvailiable(Item item, String text) {
        return Boolean.TRUE.equals(item.getAvailable())
                && (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text));
    }

}
