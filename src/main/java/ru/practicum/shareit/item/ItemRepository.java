package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();
    private Integer counter = 0;

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public Item getItemById(Integer id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        return null;
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public Item addItem(Item item) {
        int id = ++counter;
        item.setId(id);
        items.put(id, item);
        return item;
    }

    public void deleteItem(Integer itemId) {
        items.remove(itemId);
    }
}
