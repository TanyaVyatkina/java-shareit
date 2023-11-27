package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) {
        User user = findUserIfExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item = itemRepository.add(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
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
        itemRepository.update(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(int userId, int itemId) {
        findUserIfExists(userId);
        Item item = findItemIfExists(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(int userId) {
        findUserIfExists(userId);
        List<Item> foundItems = itemRepository.findByOwnerId(userId);
        return ItemMapper.toItemDtoList(foundItems);
    }

    @Override
    public List<ItemDto> searchItems(int userId, String text) {
        findUserIfExists(userId);
        List<Item> foundItems = itemRepository.search(text.toLowerCase());
        return ItemMapper.toItemDtoList(foundItems);
    }

    private User findUserIfExists(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
    }

    private Item findItemIfExists(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найденa."));
    }
}
