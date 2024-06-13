package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.Add;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    @NotNull
    private Integer itemId;

    @NotNull(groups = {Add.class})
    private LocalDateTime start;

    @NotNull(groups = {Add.class})
    private LocalDateTime end;
}
