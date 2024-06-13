package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.Add;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    @NotBlank(groups = {Add.class})
    private String name;
    @NotBlank(groups = {Add.class})
    private String description;
    @NotNull(groups = {Add.class})
    private Boolean available;

    private Integer requestId;
}
