package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ItemShortDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestBody @Validated({OnCreate.class}) ItemShortDto item) {
        log.debug("Добавление вещи {} пользователя с id = {}.", item.getName(), userId);
        ItemShortDto savedItem = (ItemShortDto) itemClient.saveItem(userId, item).getBody();
        log.debug("Вещь добавлена.");
        return savedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemShortDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                                   @RequestBody @Validated({OnUpdate.class}) ItemShortDto item) {
        log.debug("Обновление вещи id = {} пользователя c id = {}.", itemId, userId);
        ItemShortDto updatedItem = (ItemShortDto) itemClient.updateItem(userId, itemId, item).getBody();
        log.debug("Данные обновлены.");
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId) {
        log.debug("Поиск вещи id = {} пользователя c id = {}.", itemId, userId);
        ItemDto foundItem = (ItemDto) itemClient.getItem(userId, itemId).getBody();
        log.debug("Найдена вещь: {}.", foundItem);
        return foundItem;
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Поиск всех вещей пользователя id = {}.", userId);
        PageRequest page = PageRequest.of(from / size, size);
        List<ItemDto> foundItems = (List<ItemDto>) itemClient.getUserItems(userId, from, size);
        log.debug("Найдены вещи: {}.", foundItems);
        return foundItems;
    }

    @GetMapping("/search")
    public List<ItemShortDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text,
                                          @RequestParam(defaultValue = "0") @Min(0) int from,
                                          @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Поиск вещей по запросу {}.", text);
        PageRequest page = PageRequest.of(from / size, size);
        List<ItemShortDto> foundItems = (List<ItemShortDto>) itemClient.searchItems(userId, text, from, size);
        log.debug("Найдены вещи: {}.", foundItems);
        return foundItems;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                                 @RequestBody @Valid CommentDto commentDto) {
        log.debug("Запрос на добавление комментария от пользователя id = {}, к вещи id = {}.", userId, itemId);
        CommentDto comment = (CommentDto) itemClient.addComment(userId, itemId, commentDto).getBody();
        log.debug("Комментарий добавлен.");
        return comment;
    }
}
