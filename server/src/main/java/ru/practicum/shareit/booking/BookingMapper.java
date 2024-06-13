package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(BookingDto.Item.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName()
                        ).build())
                .booker(BookingDto.Booker.builder().id(booking.getBooker().getId())
                        .name(booking.getBooker().getName()
                        ).build())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBookingFromAdd(BookingDtoAdd bookingDtoAdd) {
        return Booking.builder()
                .id(bookingDtoAdd.getId())
                .start(bookingDtoAdd.getStart())
                .end(bookingDtoAdd.getEnd())
                .build();
    }
}
