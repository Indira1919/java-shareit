package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;

import java.util.List;

public interface ItemService {
    List<ItemDtoComments> getItemsOfUser(Integer userId);

    ItemDtoComments getItemById(Integer itemId, Integer userId);

    List<ItemDto> getItems(String description);

    ItemDto updateItem(ItemDto itemDto, Integer id, Integer userId);

    ItemDto addItem(ItemDto itemDto, Integer userId);

    CommentDto addComment(Integer itemId, Integer userId, CommentDto commentDto);

    void deleteItem(Integer itemId);

}
