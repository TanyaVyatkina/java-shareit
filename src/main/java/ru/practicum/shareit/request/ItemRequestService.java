package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestShortDto addRequest(long userId, ItemRequestShortDto requestDto);

    List<ItemRequestDto> getUserItemRequests(long userId);

    List<ItemRequestDto> getOtherItemRequests(long userId, int from, int size);

    ItemRequestDto getItemRequest(long userId, long itemRequestId);
}
