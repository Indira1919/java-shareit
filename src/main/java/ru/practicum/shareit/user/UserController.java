package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.valid.Add;
import ru.practicum.shareit.valid.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAll(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                 @Positive @RequestParam(defaultValue = "30") Integer size) {
        return userService.findAll(from, size);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable Integer id) {
        return userService.updateUser(userDto, id);
    }

    @PostMapping
    public UserDto addUser(@Validated(Add.class) @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }
}
