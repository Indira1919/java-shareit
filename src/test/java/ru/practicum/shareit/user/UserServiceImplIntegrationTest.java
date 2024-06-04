package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private Integer userId = 1;

    @BeforeEach
    void beforeEach() {
        User user = new User(1, "test", "test@yandex.ru");
        userId = userRepository.save(user).getId();
    }

    @Test
    void getAll() {
        List<UserDto> actualDtoList = userService.findAll(0,10);

        assertEquals(1, actualDtoList.size());
    }
    @Test
    void getUserById() {
        UserDto userDto = userService.getUserById(userId);

        assertEquals(userId, userDto.getId());
        assertEquals("test@yandex.ru", userDto.getEmail());
        assertEquals("test", userDto.getName());
    }

    @Test
    void addUser() {
        UserDto userDto = new UserDto(1, "test2", "test2@yandex.ru");

        UserDto userDto1 = userService.addUser(userDto);

        assertEquals(1, userDto.getId());
        assertEquals("test2@yandex.ru", userDto.getEmail());
        assertEquals("test2", userDto.getName());
    }

    @Test
    void updateUser() {
        UserDto userDto = new UserDto(1, "test222", "test2@yandex.ru");
        UserDto userDto1 = userService.updateUser(userDto, userId);

        assertEquals("test2@yandex.ru", userDto.getEmail());
        assertEquals("test222", userDto.getName());
    }

    @Test
    void delete() {
        userService.deleteUser(userId);
        List<UserDto> usersDto = userService.findAll(0,10);

        assertEquals(List.of(), usersDto);
    }
}
