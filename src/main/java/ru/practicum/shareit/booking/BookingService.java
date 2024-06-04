package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;

import java.util.List;

public interface BookingService {
    BookingDto getBookingById(Integer bookingId, Integer userId);

    List<BookingDto> getAllBookingOfUser(String state, Integer userId);

    List<BookingDto> getAllBookingOfOwner(String state, Integer userId);

    BookingDto getConsentToBooking(Integer bookingId, Integer userId, Boolean approved);

    BookingDto addBookingRequest(BookingDtoAdd bookingDto, Integer userId);
}
