package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDto.Booker;
import ru.practicum.shareit.booking.dto.BookingDto.Item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = start.plusDays(7);
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private final BookingDto.Booker booker = new BookingDto.Booker(1, "test");
    private final BookingDto.Item bookingItem = new BookingDto.Item(1, "testItem");


    @BeforeEach
    void beforeEach() {
    }

    @Test
    void testDto() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1,
                start,
                end,
                bookingItem,
                booker,
                Status.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");

    }

    @Test
    void testSerialize() throws Exception {
        BookingDto bookingDto = new BookingDto(1, start, end, new Item(1, "item name"),
                new Booker(1, "user name"), Status.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        Assertions.assertThat(result).hasJsonPath("$.id");
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(result).hasJsonPath("$.start");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().format(formatter));
        Assertions.assertThat(result).hasJsonPath("$.end");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().format(formatter));
        Assertions.assertThat(result).hasJsonPath("$.item");
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingDto.getItem().getName());
        Assertions.assertThat(result).hasJsonPath("$.booker");
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(bookingDto.getBooker().getName());
        Assertions.assertThat(result).hasJsonPath("$.status");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().toString());
        BookingDto bookingDtoTest = json.parseObject(result.getJson());
        Assertions.assertThat(bookingDtoTest).isEqualTo(bookingDto);
    }
}
