package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.Add;
import ru.practicum.shareit.valid.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(groups = Add.class)
    private String name;

    @Email(groups = {Add.class, Update.class})
    @NotBlank(groups = Add.class)
    private String email;
}
