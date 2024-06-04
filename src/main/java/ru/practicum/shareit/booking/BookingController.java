package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.valid.Add;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Integer bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingOfUser(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(defaultValue = "30") @Max(100) @Min(1) Integer size) {
        return bookingService.getAllBookingOfUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                                 @RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "30") @Max(100) @Min(1) Integer size) {
        return bookingService.getAllBookingOfOwner(state, userId, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto getConsentToBooking(@PathVariable Integer bookingId,
                                          @RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @RequestParam Boolean approved) {
        return bookingService.getConsentToBooking(bookingId, userId, approved);
    }

    @PostMapping
    public BookingDto addBookingRequest(@Validated(Add.class) @RequestBody BookingDtoAdd bookingDto,
                                        @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.addBookingRequest(bookingDto, userId);
    }
}
