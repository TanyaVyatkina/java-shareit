package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Validated
@Slf4j
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Validated({OnCreate.class}) ItemDto item) {
        log.debug("Добавление вещи {} пользователя с id = {}.", item.getName(), userId);
        ItemDto savedItem = itemService.addItem(userId, item);
        log.debug("Вещь добавлена.");
        return savedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                              @RequestBody @Validated({OnUpdate.class}) ItemDto item) {
        log.debug("Обновление вещи id = {} пользователя c id = {}.", itemId, userId);
        ItemDto updatedItem = itemService.updateItem(userId, itemId, item);
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
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Поиск всех вещей пользователя id = {}.", userId);
        List<ItemDto> foundItems = itemService.getUserItems(userId);
        log.debug("Найдены вещи: {}.", foundItems);
        return foundItems;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        log.debug("Поиск вещей по запросу {}.", text);
        List<ItemDto> foundItems = itemService.searchItems(userId, text);
        log.debug("Найдены вещи: {}.", foundItems);
        return foundItems;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                                 @RequestBody @Valid CommentDto commentDto) {
        log.debug("Запрос на добавление комментария от пользователя id = {}, к вещи id = {}.", userId, itemId);
        commentDto.setCreated(LocalDateTime.now());
        CommentDto comment = itemService.addComment(userId, itemId, commentDto);
        log.debug("Комментарий добавлен.");
        return comment;
    }
}
