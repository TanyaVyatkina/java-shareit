package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestShortDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody ItemRequestShortDto requestDto) {
        log.debug("Создание запроса от пользователя с id = {}.", userId);
        ItemRequestShortDto savedRequest = itemRequestService.addRequest(userId, requestDto);
        log.debug("Запрос создан.");
        return savedRequest;
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Поиск всех запросов пользователя id = {}.", userId);
        List<ItemRequestDto> foundRequests = itemRequestService.getUserItemRequests(userId);
        log.debug("Найдены запросы: {}.", foundRequests);
        return foundRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.debug("Поиск всех запросов.");
        PageRequest page = PageRequest.of(from / size, size)
                .withSort(Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequestDto> foundRequests = itemRequestService.getOtherItemRequests(userId, page);
        log.debug("Найдены запросы: {}.", foundRequests);
        return foundRequests;
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("itemRequestId") long itemRequestId) {
        log.debug("Поиск запроса с id = {}.", itemRequestId);
        ItemRequestDto foundRequest = itemRequestService.getItemRequest(userId, itemRequestId);
        log.debug("Найден запрос: {}.", foundRequest);
        return foundRequest;
    }
}
