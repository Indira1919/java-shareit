package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.valid.Add;
import ru.practicum.shareit.valid.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder(toBuilder = true)
public class User {
    private Integer id;
    @NotBlank(groups = {Add.class})
    private String name;
    @NotBlank(groups = {Add.class})
    @Email(groups = {Add.class, Update.class})
    private String email;
}
