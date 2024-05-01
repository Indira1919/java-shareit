package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.valid.Add;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemsOfUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItems(@RequestParam(value = "text", required = false) String text) {
        return itemService.getItems(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto, @PathVariable Integer itemId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @PostMapping
    public ItemDto addItem(@Validated(Add.class) @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.addItem(itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Integer itemId) {
        itemService.deleteItem(itemId);
    }
}