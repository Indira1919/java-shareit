package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getItemRequestUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getItemRequestUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "30") Integer size,
                                                    @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getAllItemRequest(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Integer requestId,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getItemRequestById(requestId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.addItemRequest(itemRequestDto, userId);
    }
}
