package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ItemRequestShortDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody @Valid ItemRequestShortDto requestDto) {
        log.debug("Создание запроса от пользователя с id = {}.", userId);
        ItemRequestShortDto savedRequest = (ItemRequestShortDto) itemRequestClient
                .addItemRequest(userId, requestDto).getBody();
        log.debug("Запрос создан.");
        return savedRequest;
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Поиск всех запросов пользователя id = {}.", userId);
        List<ItemRequestDto> foundRequests = (List<ItemRequestDto>) itemRequestClient
                .getUserItemRequests(userId).getBody();
        log.debug("Найдены запросы: {}.", foundRequests);
        return foundRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Поиск всех запросов.");
        List<ItemRequestDto> foundRequests = (List<ItemRequestDto>) itemRequestClient
                .getOtherItemRequests(userId, from, size).getBody();
        log.debug("Найдены запросы: {}.", foundRequests);
        return foundRequests;
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("itemRequestId") long itemRequestId) {
        log.debug("Поиск запроса с id = {}.", itemRequestId);
        ItemRequestDto foundRequest = (ItemRequestDto) itemRequestClient.getItemRequest(userId, itemRequestId).getBody();
        log.debug("Найден запрос: {}.", foundRequest);
        return foundRequest;
    }
}
