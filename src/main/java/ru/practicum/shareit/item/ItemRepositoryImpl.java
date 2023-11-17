package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private int itemId = 0;
    private Map<Integer, Item> items = new HashMap<>();
    private Map<Integer, Set<Item>> itemsByOwner = new HashMap<>();


    @Override
    public Item add(Item item) {
        int newId = getItemId();
        int ownerId = item.getOwner().getId();

        item.setId(newId);
        items.put(newId, item);
        if (itemsByOwner.get(ownerId) == null) {
            itemsByOwner.put(ownerId, new HashSet<>());
        }
        itemsByOwner.get(ownerId).add(item);
        return item;
    }

    @Override
    public Optional<Item> findById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
        itemsByOwner.get(item.getOwner().getId()).remove(item);
        itemsByOwner.get(item.getOwner().getId()).add(item);
    }

    @Override
    public List<Item> findByOwnerId(int ownerId) {
        return new ArrayList<>(itemsByOwner.get(ownerId));
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return items.values()
                .stream()
                .filter(i -> isMatch(i, text))
                .collect(Collectors.toList());
    }

    private int getItemId() {
        return ++itemId;
    }

    private boolean isMatch(Item item, String text) {
        return Boolean.TRUE.equals(item.getAvailable())
                && (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text));
    }

}
