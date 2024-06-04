package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto getBookingById(Integer bookingId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Нет доступа к данным бронирования");
        }

        return BookingMapper.toBookingDto(booking); // из booking в BookingDto
    }

    @Override
    public List<BookingDto> getAllBookingOfUser(String state, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        List<Booking> booking;
        State stateOfBooking = State.valueOf(state);

        switch (stateOfBooking) {
            case ALL:
                booking = bookingRepository.findAllByBooker(user, sort);
                break;
            case CURRENT:
                booking = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                booking = bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                booking = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                booking = bookingRepository.findAllByBookerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                booking = bookingRepository.findAllByBookerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return booking.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingOfOwner(String state, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        List<Booking> booking;
        State stateOfBooking = State.valueOf(state);

        switch (stateOfBooking) {
            case ALL:
                booking = bookingRepository.findAllByItemOwner(user, sort);
                break;
            case CURRENT:
                booking = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                booking = bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                booking = bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                booking = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                booking = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return booking.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto getConsentToBooking(Integer bookingId, Integer userId, Boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Нет доступа к данным бронирования");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Вы не можете изменить статус");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        booking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking); // из booking в BookingDto
    }

    @Override
    @Transactional
    public BookingDto addBookingRequest(BookingDtoAdd bookingDtoAdd, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDtoAdd.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена"));

        if (bookingDtoAdd.getEnd().isBefore(bookingDtoAdd.getStart()) ||
                bookingDtoAdd.getStart().isAfter(bookingDtoAdd.getEnd()) ||
                bookingDtoAdd.getStart().equals(bookingDtoAdd.getEnd())) {
            throw new BadRequestException("Даты введены не правильно");
        }

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Вы не можете забронировать свою вещь");
        }

        Booking booking = BookingMapper.toBookingFromAdd(bookingDtoAdd);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

}
