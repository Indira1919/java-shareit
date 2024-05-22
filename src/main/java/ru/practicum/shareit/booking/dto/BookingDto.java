package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    private Integer id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Item item;

    private Booker booker;

    private Status status;

    @Data
    public static class Booker {
        private final Integer id;
        private final String name;
    }

    @Data
    public static class Item {
        private final Integer id;
        private final String name;
    }
}
