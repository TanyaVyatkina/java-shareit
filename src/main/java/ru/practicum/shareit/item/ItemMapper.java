package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemShortDto toItemShortDto(Item item) {
        Long requestId = item.getItemRequest() == null ? null : item.getItemRequest().getId();
        return new ItemShortDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getOwner().getId(), requestId);
    }

    public static ItemDto toItemDto(Item item, List<CommentDto> comments, List<Booking> bookingsForItem) {
        ItemDto dto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getOwner().getId(), comments);
        if (bookingsForItem == null) {
            return dto;
        }
        Booking nextBooking = bookingsForItem
                .stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()) && !BookingStatus.REJECTED.equals(b.getStatus()))
                .sorted((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                .findFirst()
                .orElse(null);
        Booking lastBooking = bookingsForItem
                .stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()) && !BookingStatus.REJECTED.equals(b.getStatus()))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .findFirst()
                .orElse(null);
        dto.setLastBooking(lastBooking != null ? BookingMapper.toBookingShortDto(lastBooking) : null);
        dto.setNextBooking(nextBooking != null ? BookingMapper.toBookingShortDto(nextBooking) : null);
        return dto;
    }

    public static List<ItemShortDto> toItemShortDtoList(List<Item> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList());
    }

    public static Item toItem(ItemShortDto itemDto, User owner, ItemRequest itemRequest) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setId(item.getId());
        item.setItemRequest(itemRequest);
        return item;
    }

    public static ItemForBookingDto toItemForBookingDto(Item item) {
        return new ItemForBookingDto(item.getId(), item.getName());
    }
}
