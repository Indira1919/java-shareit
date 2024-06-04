package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    public List<ItemRequestDto> getAllItemRequest(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "30") @Min(1) @Max(100) Integer size,
                                            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAllItemRequest(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Integer requestId,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }
}
