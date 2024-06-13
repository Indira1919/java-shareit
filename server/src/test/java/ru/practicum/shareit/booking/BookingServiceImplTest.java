package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private User user1;
    private User user2;
    private Item item;
    private Booking booking;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(7);

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "test", "test.@yandex.ru");
        user2 = new User(2, "test2", "2test2.@mail.ru");
        item = new Item(1, "ppp", "description", true, user1,
                null);
        booking = new Booking(1, start, end, item, user1, Status.WAITING);
    }

    @Test
    void getBookingById() {
        Integer bookingId = 1;
        Integer userId = 1;
        Mockito.when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));

        BookingDto result = bookingService.getBookingById(bookingId, userId);

        assertEquals(1, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(Status.WAITING, result.getStatus());
        assertEquals(1, result.getItem().getId());
        assertEquals("ppp", result.getItem().getName());
        assertEquals(1, result.getBooker().getId());
        assertEquals("test", result.getBooker().getName());
    }

    @Test
    void getByIdObjectNotFoundException() {
        Integer bookingId = 1000;
        Integer userId = 1;

        Mockito.when(bookingRepository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException("Бронирование не найдено"));
        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));

    }

    @Test
    void getAllBookingOfOwnerStateAll() {
        Integer userId = 1;
        String state = String.valueOf(State.ALL);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwner(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfOwner(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfOwnerStateCurrent() {
        Integer userId = 1;
        String state = String.valueOf(State.CURRENT);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfOwner(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfOwnerStatePast() {
        Integer userId = 1;
        String state = String.valueOf(State.PAST);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndEndBefore(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfOwner(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfOwnerStateFuture() {
        Integer userId = 1;
        String state = String.valueOf(State.FUTURE);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStartAfter(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfOwner(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfOwnerStateWaiting() {
        Integer userId = 1;
        String state = String.valueOf(State.WAITING);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStatusEquals(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfOwner(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfOwnerStateRejected() {
        Integer userId = 1;
        String state = String.valueOf(State.REJECTED);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStatusEquals(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfOwner(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfOwnerStateUnsupportedStatus() {
        Integer userId = 1;
        String state = "UNSUPPORTED_STATUS";

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));

        assertThrows(BadRequestException.class, () ->
                bookingService.getAllBookingOfOwner(state, userId, 0, 10));
    }

    @Test
    void getAllBookingOfOwnerStateAllObjectNotFoundException() {
        Integer userId = 1000;
        String state = String.valueOf(State.ALL);

        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException("Пользователь не найден"));

        assertThrows(ObjectNotFoundException.class, () ->
                bookingService.getAllBookingOfOwner(state, userId, 0, 10));
    }


    @Test
    void getAllBookingOfUserStateAll() {
        Integer userId = 1;
        String state = String.valueOf(State.ALL);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByBooker(Mockito.any(), Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfUser(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfUserStateCurrent() {
        Integer userId = 1;
        String state = String.valueOf(State.CURRENT);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfUser(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfUserStatePast() {
        Integer userId = 1;
        String state = String.valueOf(State.PAST);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByBookerAndEndBefore(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfUser(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfUserStateFuture() {
        Integer userId = 1;
        String state = String.valueOf(State.FUTURE);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByBookerAndStartAfter(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfUser(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfUserStateWaiting() {
        Integer userId = 1;
        String state = String.valueOf(State.WAITING);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByBookerAndStatusEquals(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfUser(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfUserStateRejected() {
        Integer userId = 1;
        String state = String.valueOf(State.REJECTED);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllByBookerAndStatusEquals(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookingOfUser(state, userId, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingOfUserStateUnsupportedStatus() {
        Integer userId = 1;
        String state = "UNSUPPORTED_STATUS";

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));

        assertThrows(BadRequestException.class, ()
                -> bookingService.getAllBookingOfUser(state, userId, 0, 10));
    }

    @Test
    void getAllBookingOfUserPageNegative() {
        Integer userId = 1;
        String state = String.valueOf(State.ALL);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));

        assertThrows(BadRequestException.class, ()
                -> bookingService.getAllBookingOfUser(state, userId, -1, 10));
    }

    @Test
    void getAllBookingOfUserStateAllObjectNotFoundException() {
        Integer userId = 3;
        String state = String.valueOf(State.ALL);

        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException("Пользователь не найден"));

        assertThrows(ObjectNotFoundException.class, ()
                -> bookingService.getAllBookingOfUser(state, userId, 0, 10));
    }


    @Test
    void addBookingRequest() {
        BookingDtoAdd dto = new BookingDtoAdd(user2.getId(), start, end, item.getId());

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        BookingDto test = bookingService.addBookingRequest(dto, user2.getId());

        assertThat(test).hasFieldOrProperty("id");
    }

    @Test
    void addBookingRequestAvailableFalse() {
        item = new Item(1, "ppp", "description", false, user1,
                null);
        BookingDtoAdd dto = new BookingDtoAdd(user2.getId(), start, end, item.getId());

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

        assertThrows(BadRequestException.class, ()
                -> bookingService.addBookingRequest(dto, user2.getId()));
    }

    @Test
    void addBookingRequestData() {
        item = new Item(1, "ppp", "description", true, user1,
                null);
        BookingDtoAdd dto = new BookingDtoAdd(user2.getId(), LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(7), item.getId());

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

        assertThrows(BadRequestException.class, ()
                -> bookingService.addBookingRequest(dto, user2.getId()));
    }

    @Test
    void addBookingRequestDataEquals() {
        item = new Item(1, "ppp", "description", true, user1,
                null);
        BookingDtoAdd dto = new BookingDtoAdd(user2.getId(),
                LocalDateTime.of(2028,10,10,10,10,10),
                LocalDateTime.of(2028,10,10,10,10,10), item.getId());

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

        assertThrows(BadRequestException.class, ()
                -> bookingService.addBookingRequest(dto, user2.getId()));
    }

    @Test
    void addBookingRequestOwner() {
        item = new Item(1, "ppp", "description", true, user1,
                null);
        BookingDtoAdd dto = new BookingDtoAdd(user1.getId(), start, end, item.getId());

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        assertThrows(ObjectNotFoundException.class, ()
                -> bookingService.addBookingRequest(dto, user1.getId()));
    }

    @Test
    void getConsentToBooking() {
        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getConsentToBooking(booking.getId(), user1.getId(), true);

        assertEquals(bookingDto.getStatus(), (Status.APPROVED));
    }

    @Test
    void getConsentToBookingStatusRejected() {
        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getConsentToBooking(booking.getId(), user1.getId(), false);

        assertEquals(bookingDto.getStatus(), (Status.REJECTED));
    }

    @Test
    void getConsentToBookingObjectNotFoundException() {
        Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class, ()
                -> bookingService.getConsentToBooking(booking.getId(), user2.getId(), true));
    }

    @Test
    void getConsentToBookingBadRequestException() {
        booking = new Booking(1, start, end, item, user1, Status.REJECTED);
        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, ()
                -> bookingService.getConsentToBooking(booking.getId(), user1.getId(), true));
    }
}
