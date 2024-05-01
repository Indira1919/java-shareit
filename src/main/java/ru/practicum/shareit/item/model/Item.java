package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.valid.Add;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Item {
    private Integer id;
    @NotBlank(groups = {Add.class})
    private String name;
    @NotBlank(groups = {Add.class})
    private String description;
    @NotNull(groups = {Add.class})
    private Boolean available;
    private User owner;
    private ItemRequest request;
}

