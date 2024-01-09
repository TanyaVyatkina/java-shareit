package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestBody @Valid ItemRequestShortDto requestDto) {
        log.debug("Создание запроса от пользователя с id = {}.", userId);
        ResponseEntity savedRequest = itemRequestClient.addItemRequest(userId, requestDto);
        log.debug("Запрос создан.");
        return savedRequest;
    }

    @GetMapping
    public ResponseEntity getUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Поиск всех запросов пользователя id = {}.", userId);
        ResponseEntity foundRequests = itemRequestClient.getUserItemRequests(userId);
        log.debug("Найдены запросы: {}.", foundRequests);
        return foundRequests;
    }

    @GetMapping("/all")
    public ResponseEntity getOtherItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Поиск всех запросов.");
        ResponseEntity foundRequests = itemRequestClient.getOtherItemRequests(userId, from, size);
        log.debug("Найдены запросы: {}.", foundRequests.getBody());
        return foundRequests;
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("itemRequestId") long itemRequestId) {
        log.debug("Поиск запроса с id = {}.", itemRequestId);
        ResponseEntity foundRequest = itemRequestClient.getItemRequest(userId, itemRequestId);
        log.debug("Найден запрос: {}.", foundRequest);
        return foundRequest;
    }
}
