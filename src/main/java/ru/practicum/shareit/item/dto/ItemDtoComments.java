package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.Add;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoComments {
    private Integer id;
    @NotBlank(groups = {Add.class})
    private String name;
    @NotBlank(groups = {Add.class})
    private String description;
    @NotNull(groups = {Add.class})
    private Boolean available;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;

    @Data
    public static class BookingDto {
        private final Integer id;
        private final Integer bookerId;
    }
}
