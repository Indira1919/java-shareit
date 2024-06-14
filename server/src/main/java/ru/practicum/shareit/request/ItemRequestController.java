package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getItemRequestUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequest(@RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "30") Integer size,
                                                  @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAllItemRequest(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Integer requestId,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }
}
