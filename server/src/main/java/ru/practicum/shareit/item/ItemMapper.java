package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item toItemUpdate(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(itemDto.getId() != null ? itemDto.getId() : item.getId())
                .name(itemDto.getName() != null && !itemDto.getName().isBlank() ? itemDto.getName() : item.getName())
                .description(itemDto.getDescription() != null && !itemDto.getDescription().isBlank() ?
                        itemDto.getDescription() : item.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }

    public static ItemDtoComments toItemDtoComments(Item item) {
        return ItemDtoComments.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemDtoRequest toItemDtoRequest(Item item) {
        return ItemDtoRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }
}

