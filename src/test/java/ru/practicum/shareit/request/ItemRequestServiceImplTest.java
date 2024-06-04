package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void addItemRequest() {
        Integer userId = 1;
        User user = new User(userId, "test", "test@yandex.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "desc", null, null);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemRequestDto dto = itemRequestService.addItemRequest(itemRequestDto, userId);

        assertThat(dto.getDescription()).isEqualTo(itemRequestDto.getDescription());
    }

    @Test
    void addItemRequestObjectNotFoundException() {
        Integer userId = 1000;

        User user = new User(userId, "test", "test@yandex.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "desc", null, null);

        Mockito.when(userRepository.findById(Mockito.any()))
                .thenThrow(new ObjectNotFoundException("Пользователь не найден"));

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.addItemRequest(itemRequestDto, userId));

        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getAllByRequester_shouldReturnItemRequestDtoList() {
        Integer userId = 1;

        User user = new User(userId, "test", "test@yandex.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "desc",
                LocalDateTime.of(2024,5,6, 20, 20, 20), List.of());

        ItemRequest itemRequest = new ItemRequest(1, "desc", user, null);


        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findAllByRequestor(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequestDto1 = List.of(itemRequestDto);
        List<ItemRequestDto> itemRequestDto2 = itemRequestService.getItemRequestUser(userId);

        assertEquals(itemRequestDto1.get(0).getId(), itemRequestDto2.get(0).getId());
        assertEquals(itemRequestDto1.get(0).getDescription(), itemRequestDto2.get(0).getDescription());
        assertEquals(itemRequestDto1.get(0).getItems().size(), itemRequestDto2.get(0).getItems().size());
    }

    @Test
    void getItemRequestUserObjectNotFoundException() {
        Integer userId = 999;

        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException("Пользователь не найден"));

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getItemRequestUser(userId));

        Mockito.verify(itemRequestRepository, Mockito.never()).findAllByRequestor(Mockito.any(), Mockito.any());
    }

    @Test
    void getAllItemRequest() {
        Integer userId = 1;
        Integer userId2 = 2;

        User user = new User(userId, "test", "test@yandex.ru");
        User user2 = new User(userId, "test2", "test2@yandex.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "desc",
                LocalDateTime.of(2024,5,6, 20, 20, 20), List.of());
        ItemRequest itemRequest = new ItemRequest(1, "desc", user,
                LocalDateTime.of(2024,5,6, 20, 20, 20));


        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user2));
        Mockito.when(itemRequestRepository.findAllByRequestorNot(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequestDto1 = List.of(itemRequestDto);
        List<ItemRequestDto> itemRequestDto2 = itemRequestService.getAllItemRequest(0, 10, userId2);

        assertEquals(itemRequestDto1.get(0).getId(), itemRequestDto2.get(0).getId());
        assertEquals(itemRequestDto1.get(0).getDescription(), itemRequestDto2.get(0).getDescription());
        assertEquals(itemRequestDto1.get(0).getItems().size(), itemRequestDto2.get(0).getItems().size());
    }

    @Test
    void getItemRequestById() {
        Integer userId = 1;
        Integer requestId = 1;

        User user = new User(userId, "test", "test@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(1, "desc", user,
                LocalDateTime.of(2024,5,6, 20, 20, 20));

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(Mockito.anyInt())).thenReturn(List.of());

        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "desc",
                LocalDateTime.of(2024,5,6, 20, 20, 20), List.of());
        ItemRequestDto itemRequestDto2 = itemRequestService.getItemRequestById(requestId, userId);

        assertEquals(itemRequestDto, itemRequestDto2);
    }

    @Test
    void getItemRequestByIdObjectNotFoundException() {
        Integer userId = 1;
        Integer requestId = 1000;

        Mockito.when(itemRequestRepository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException("Запрос не найден"));

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getItemRequestById(requestId, userId));
    }

}
