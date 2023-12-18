package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestShortDto addRequest(long userId, ItemRequestShortDto requestDto) {
        User user = findUserIfExists(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, user);
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestShortDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(long userId) {
        findUserIfExists(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestor_Id(userId);
        if (itemRequests.isEmpty()) return Collections.emptyList();
        return getItemRequestsDtoWithItems(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getOtherItemRequests(long userId, PageRequest page) {
        findUserIfExists(userId);
        return getItemRequestsDtoWithItems(itemRequestRepository.findByRequestor_IdNot(userId, page));
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long itemRequestId) {
        findUserIfExists(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + itemRequestId + " не найден."));
        List<Item> itemsForRequest = itemRepository.findByItemRequest_Id(itemRequestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest, ItemMapper.toItemShortDtoList(itemsForRequest));
    }

    private List<ItemRequestDto> getItemRequestsDtoWithItems(List<ItemRequest> itemRequests) {
        List<Long> requestsIds = itemRequests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> itemsForRequests = itemRepository.findByItemRequest_IdIn(requestsIds);
        Map<ItemRequest, List<Item>> itemsMap = itemsForRequests
                .stream()
                .collect(groupingBy(Item::getItemRequest, toList()));
        List<ItemRequestDto> foundRequests = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemShortDto> itemsForRequest = ItemMapper.toItemShortDtoList(itemsMap.get(itemRequest));
            foundRequests.add(ItemRequestMapper.toItemRequestDto(itemRequest, itemsForRequest));
        }
        return foundRequests;
    }

    private User findUserIfExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
    }
}
