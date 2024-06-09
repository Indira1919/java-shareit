package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> getItemRequestUser(Integer userId);

    List<ItemRequestDto> getAllItemRequest(Integer from, Integer size, Integer userId);

    ItemRequestDto getItemRequestById(Integer requestId, Integer userId);

    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Integer userId);
}
