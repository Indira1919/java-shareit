package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemDto itemDto;
    private Item item;
    private ItemDtoComments itemDtoComments;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;
    private ItemRequest itemRequest;

    @BeforeEach
    void create() {
        user = new User(1, "test", "test@yandex.ru");

        item = new Item(1, "name", "description", true, user,
                null);

        itemDto = new ItemDto(1, "name", "description", true, 1);

        itemDtoComments = new ItemDtoComments(1, "name", "description", true,
                null, null, null);

        itemRequest = new ItemRequest(1, "description", new User(), null);

        booking = new Booking(1, null, null, item, user, Status.WAITING);

        comment = new Comment(1, "comment", item, user, null);
        commentDto = new CommentDto(1, "comment", user.getName(), null);

    }

    @Test
    void getItemsOfUser() {

        Integer userId = 1;

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(0, 10)))
                .thenReturn(List.of(item));

        Mockito.when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatus(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusEquals(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        Mockito.when(commentRepository.findAllByItemId(Mockito.anyInt())).thenReturn(List.of(comment));

        itemDtoComments = ItemMapper.toItemDtoComments(item);
        itemDtoComments.setComments(List.of(CommentMapper.toCommentDto(comment)));
        itemDtoComments.setLastBooking(new ItemDtoComments.BookingDto(booking.getId(), booking.getBooker().getId()));
        itemDtoComments.setNextBooking(new ItemDtoComments.BookingDto(booking.getId(), booking.getBooker().getId()));

        List<ItemDtoComments> itemDtoComments1 = List.of(itemDtoComments);
        List<ItemDtoComments> itemDtoComments2 = itemService.getItemsOfUser(userId, 0, 10);

        Assertions.assertEquals(itemDtoComments1, itemDtoComments2);
    }

    @Test
    void getItemById() {
        Integer itemId = 1;
        Integer userId = 1;

        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(Mockito.anyInt())).thenReturn(List.of(comment));

        itemDtoComments = ItemMapper.toItemDtoComments(item);
        itemDtoComments.setComments(List.of(CommentMapper.toCommentDto(comment)));

        assertThat(itemService.getItemById(itemId, userId)).isEqualTo(itemDtoComments);
    }

    @Test
    void getItemByIdObjectNotFoundException() {
        Integer itemId = 1000;
        Integer userId = 1;

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException("Вещь не найдена"));

        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemById(itemId, userId));
    }

    @Test
    void getItems() {
        Mockito.when(itemRepository
                        .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable("text",
                                "text", true, PageRequest.of(0, 10)))
                .thenReturn(List.of(item));

        List<ItemDto> itemDto1 = List.of(ItemMapper.toItemDto(item));
        List<ItemDto> itemDto2 = itemService.getItems("text", 0, 10);

        assertEquals(itemDto1, itemDto2);
    }

    @Test
    void addItemWithItemRequest() {
        Integer userId = 1;

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto itemDto1 = new ItemDto(1, "name", "description", true, 1);
        ItemDto itemDto2 = itemService.addItem(itemDto, userId);

        assertEquals(itemDto1, itemDto2);

        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void addItemOwnerNotFound() {
        Integer userId = 999;

        Mockito.when(userRepository.findById(userId)).
                thenThrow(new ObjectNotFoundException("Пользователь не найден"));

        assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(itemDto, userId));

        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateItem() {
        Integer itemId = 1;
        Integer userId = 1;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        ItemDto itemDto1 = new ItemDto(itemId, "testtt", "description", true, null);

        itemService.updateItem(itemDto1, itemId, userId);

        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void deleteItem() {
        Integer itemId = 1;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(itemId);
    }
}
