package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.Add;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoAdd {

    private Integer id;

    @NotNull(groups = {Add.class})
    @FutureOrPresent(groups = {Add.class})
    private LocalDateTime start;

    @NotNull(groups = {Add.class})
    @FutureOrPresent(groups = {Add.class})
    private LocalDateTime end;

    @NotNull
    private Integer itemId;
}
