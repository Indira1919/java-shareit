package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final ItemServiceImpl itemService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private Integer itemId;
    private Integer userId;
    private Integer commentId;
    private User user;
    private Item item;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        user = new User(null, "test", "test@yandex.ru");
        user = userRepository.save(user);
        userId = user.getId();

        item = new Item(null, "item", "description", true, user,
                null);
        item = itemRepository.save(item);
        itemId = item.getId();

        comment = new Comment(null, "comment", item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);
        commentId = comment.getId();
    }

    @Test
    void getAll() {
        ItemDtoComments itemDto = ItemMapper.toItemDtoComments(item);
        itemDto.setComments(List.of(CommentMapper.toCommentDto(comment)));

        List<ItemDtoComments> itemDtoComments = itemService.getItemsOfUser(userId, 0, 10);
        List<ItemDtoComments> itemDtoComments1 = List.of(itemDto);

        Assertions.assertEquals(itemDtoComments1.get(0).getId(), itemDtoComments.get(0).getId());
    }

    @Test
    void getById() {
        ItemDtoComments itemDto = ItemMapper.toItemDtoComments(item);
        itemDto.setComments(List.of(CommentMapper.toCommentDto(comment)));

        ItemDtoComments itemDtoComments = itemService.getItemById(itemId, userId);

        Assertions.assertEquals(itemDto.getId(), itemDtoComments.getId());
    }

    @Test
    void addItem() {
        ItemDto itemDto = new ItemDto(null, "name", "description",
                true, null);

        ItemDto itemDto1 = itemService.addItem(itemDto, userId);

        Assertions.assertEquals(itemId + 1, itemDto1.getId());
        Assertions.assertEquals("name", itemDto1.getName());
        Assertions.assertEquals(true, itemDto1.getAvailable());
    }

    @Test
    void deleteItem() {
        itemService.deleteItem(itemId);
        List<ItemDtoComments> itemDtoComments = itemService.getItemsOfUser(userId, 0, 10);

        Assertions.assertEquals(List.of(), itemDtoComments);
    }
}
