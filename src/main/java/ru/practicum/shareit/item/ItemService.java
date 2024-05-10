package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsOfUser(Integer userId);

    ItemDto getItemById(Integer itemId);

    List<ItemDto> getItems(String description);

    ItemDto updateItem(ItemDto itemDto, Integer id, Integer userId);

    ItemDto addItem(ItemDto itemDto, Integer userId);

    void deleteItem(Integer itemId);

}
