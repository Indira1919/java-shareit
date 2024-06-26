package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void serialize() throws Exception {
        UserDto userDto = new UserDto(1, "test", "test@yandex.ru");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());

        UserDto userDtoForTest = json.parseObject(result.getJson());

        assertThat(userDtoForTest).isEqualTo(userDto);
    }
}