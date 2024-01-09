package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemShortDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestBody ItemShortDto item) {
        log.debug("Добавление вещи {} пользователя с id = {}.", item.getName(), userId);
        ItemShortDto savedItem = itemService.addItem(userId, item);
        log.debug("Вещь добавлена.");
        return savedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemShortDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                                   @RequestBody ItemShortDto item) {
        log.debug("Обновление вещи id = {} пользователя c id = {}.", itemId, userId);
        ItemShortDto updatedItem = itemService.updateItem(userId, itemId, item);
        log.debug("Данные обновлены.");
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId) {
        log.debug("Поиск вещи id = {} пользователя c id = {}.", itemId, userId);
        ItemDto foundItem = itemService.getItem(userId, itemId);
        log.debug("Найдена вещь: {}.", foundItem);
        return foundItem;
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        log.debug("Поиск всех вещей пользователя id = {}.", userId);
        PageRequest page = PageRequest.of(from / size, size).withSort(Sort.Direction.ASC, "id");
        List<ItemDto> foundItems = itemService.getUserItems(userId, page);
        log.debug("Найдены вещи: {}.", foundItems);
        return foundItems;
    }

    @GetMapping("/search")
    public List<ItemShortDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        log.debug("Поиск вещей по запросу {}.", text);
        PageRequest page = PageRequest.of(from / size, size);
        List<ItemShortDto> foundItems = itemService.searchItems(userId, text, page);
        log.debug("Найдены вещи: {}.", foundItems);
        return foundItems;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.debug("Запрос на добавление комментария от пользователя id = {}, к вещи id = {}.", userId, itemId);
        CommentDto comment = itemService.addComment(userId, itemId, commentDto);
        log.debug("Комментарий добавлен.");
        return comment;
    }
}
