package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestServiceImpl itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private ItemRequestDto itemRequestDto;
    private ItemDtoRequest itemDtoRequest;
    private Item item;
    private Integer userId;
    private Integer requestId;
    private LocalDateTime created;

    @BeforeEach
    void beforeEach() {
        created = LocalDateTime.now();
        User user = new User(null, "test", "test1111@yandex.ru");
        userRepository.save(user);
        userId = user.getId();

        ItemRequest itemRequest = new ItemRequest(null, "description", user, created);
        itemRequest = itemRequestRepository.save(itemRequest);
        requestId = itemRequest.getId();

        item = new Item(1, "test222", "desc", true, user, itemRequest);

        itemRequestDto = new ItemRequestDto(requestId, "description", created, List.of());

        itemDtoRequest = new ItemDtoRequest(1, "test222", "desc", true, requestId);
    }

    @Test
    void getById() {
        ItemRequestDto itemRequestDto1 = itemRequestService.getItemRequestById(requestId, userId);
        itemRequestDto1.setItems(List.of(ItemMapper.toItemDtoRequest(item)));

        assertEquals(requestId, itemRequestDto1.getId());
        assertEquals("description", itemRequestDto1.getDescription());
        assertEquals(List.of(itemDtoRequest), itemRequestDto1.getItems());
    }

    @Test
    void getItemRequestUser() {
        Integer from = 0;
        Integer size = 10;

        List<ItemRequestDto> result = itemRequestService.getItemRequestUser(userId);
        List<ItemRequestDto> dtoList = List.of(itemRequestDto);

        assertEquals(dtoList.get(0).getId(), result.get(0).getId());
        assertEquals(dtoList.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(dtoList.get(0).getItems().size(), result.get(0).getItems().size());
    }
}
