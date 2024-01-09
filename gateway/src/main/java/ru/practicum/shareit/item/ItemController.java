package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody @Validated({OnCreate.class}) ItemShortDto item) {
        log.debug("Добавление вещи {} пользователя с id = {}.", item.getName(), userId);
        ResponseEntity savedItem = itemClient.saveItem(userId, item);
        log.debug("Вещь добавлена.");
        return savedItem;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                                     @RequestBody @Validated({OnUpdate.class}) ItemShortDto item) {
        log.debug("Обновление вещи id = {} пользователя c id = {}.", itemId, userId);
        ResponseEntity updatedItem = itemClient.updateItem(userId, itemId, item);
        log.debug("Данные обновлены.");
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId) {
        log.debug("Поиск вещи id = {} пользователя c id = {}.", itemId, userId);
        ResponseEntity foundItem = itemClient.getItem(userId, itemId);
        log.debug("Найдена вещь: {}.", foundItem.getBody());
        return foundItem;
    }

    @GetMapping
    public ResponseEntity getUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "0") @Min(0) int from,
                                       @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Поиск всех вещей пользователя id = {}.", userId);
        ResponseEntity foundItems = itemClient.getUserItems(userId, from, size);
        log.debug("Найдены вещи: {}.", foundItems.getBody());
        return foundItems;
    }

    @GetMapping("/search")
    public ResponseEntity searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text,
                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Поиск вещей по запросу {}.", text);
        ResponseEntity foundItems = itemClient.searchItems(userId, text, from, size);
        log.debug("Найдены вещи: {}.", foundItems.getBody());
        return foundItems;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                                     @RequestBody @Valid CommentDto commentDto) {
        log.debug("Запрос на добавление комментария от пользователя id = {}, к вещи id = {}.", userId, itemId);
        ResponseEntity comment = itemClient.addComment(userId, itemId, commentDto);
        log.debug("Комментарий добавлен.");
        return comment;
    }
}
