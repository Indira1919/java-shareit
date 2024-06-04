package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void findAll() {
        Integer from = 0;
        Integer size = 20;

        UserDto userDto = new UserDto(1, "test", "test@yandex.ru");

        Mockito.when(userService.findAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(userDto));

        String result = mockMvc.perform(get("/users")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(userDto)), result);
    }

    @SneakyThrows
    @Test
    void getById() {
        Integer userId = 1;
        UserDto userDto = new UserDto(1, "test", "test@yandex.ru");

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void addUser() {
        Integer userId = 1;
        UserDto userDto = new UserDto(1, "test", "test@yandex.ru");

        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        Mockito.verify(userService).addUser(userDto);
    }

    @SneakyThrows
    @Test
    void addUserNameNotValid() {
        Integer userId = 1;
        UserDto userDto = new UserDto(1, null, "test@yandex.ru");

        mockMvc.perform(post("/users", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).addUser(userDto);
    }


    @SneakyThrows
    @Test
    void updateUserEmailNotValid() {
        Integer userId = 1;
        UserDto userDto = new UserDto(1, "test", "test.@yandex.ru");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).updateUser(userDto, userId);
    }

    @SneakyThrows
    @Test
    void deleteById() {
        Integer userId = 1;

        mockMvc.perform(delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUser(userId);
    }
}