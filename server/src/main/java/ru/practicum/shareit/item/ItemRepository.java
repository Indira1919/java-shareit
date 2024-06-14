package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerId(Integer ownerId, Pageable pageable);

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String description,
                                                                                              String text,
                                                                                              Boolean isAvailable,
                                                                                              Pageable pageable);

    List<Item> findAllByRequestId(Integer requestId);
}
