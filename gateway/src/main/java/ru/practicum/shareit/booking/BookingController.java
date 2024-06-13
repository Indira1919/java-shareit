package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.valid.Add;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Integer bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingOfUser(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                      @RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(defaultValue = "30") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllBookingOfUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingOfOwner(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                       @RequestHeader("X-Sharer-User-Id") Integer userId,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "30") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllBookingOfOwner(state, userId, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> getConsentToBooking(@PathVariable Integer bookingId,
                                                      @RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @RequestParam Boolean approved) {
        return bookingClient.getConsentToBooking(bookingId, userId, approved);
    }

    @PostMapping
    public ResponseEntity<Object> addBookingRequest(@Validated(Add.class) @RequestBody BookItemRequestDto requestDto,
                                                    @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingClient.addBookingRequest(requestDto, userId);
    }
}
