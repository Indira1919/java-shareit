package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getItemsOfUser(Integer userId) {
        if (userRepository.getUserById(userId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        if (itemRepository.getItemById(itemId) == null) {
            throw new ObjectNotFoundException("Вещь не найдена");
        }

        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItems(String description) {
        List<ItemDto> items = new ArrayList<>();

        if (description != null && !description.isBlank()) {
            for (Item item : itemRepository.findAll()) {
                if ((item.getName().toLowerCase().contains(description.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(description.toLowerCase())) &&
                        item.getAvailable()) {
                    items.add(ItemMapper.toItemDto(item));
                }
            }
        }

        return items;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer id, Integer userId) {
        Item item = itemRepository.getItemById(id);

        if (item == null) {
            throw new ObjectNotFoundException("Вещь не найдена");
        }

        if (userRepository.getUserById(userId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        if (!item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Невозможно обновить данные");
        }

        item = itemRepository.updateItem(ItemMapper.toItemUpdate(itemDto, item));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        if (userRepository.getUserById(userId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.getUserById(userId));

        return ItemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public void deleteItem(Integer itemId) {
        if (itemRepository.getItemById(itemId) == null) {
            throw new ObjectNotFoundException("Вещь не найдена");
        }

        itemRepository.deleteItem(itemId);
    }
}
