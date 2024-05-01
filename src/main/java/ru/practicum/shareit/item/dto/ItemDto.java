package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.valid.Add;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank(groups = {Add.class})
    private String name;
    @NotBlank(groups = {Add.class})
    private String description;
    @NotNull(groups = {Add.class})
    private Boolean available;
    private ItemRequest request;
}

