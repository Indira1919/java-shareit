package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(UserMapper.toUserDto(user));
        }

        return users;
    }

    @Override
    public UserDto getUserById(Integer id) {
        if (userRepository.getUserById(id) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer id) {
        User user = userRepository.getUserById(id);

        if (user == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().equals(user.getEmail())) {
                for (User us : userRepository.findAll()) {
                    if (userDto.getEmail().equals(us.getEmail())) {
                        if (!userDto.getId().equals(us.getId())) {
                            throw new ValidationException("Ошибка данных, этот Email уже используется");
                        }
                    }
                }
            }
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userRepository.updateUser(user));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        for (User user : userRepository.findAll()) {
            if (userDto.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Ошибка данных, этот Email уже используется");
            }
        }

        User user = UserMapper.toUser(userDto);

        return UserMapper.toUserDto(userRepository.addUser(user));
    }

    @Override
    public void deleteUser(Integer id) {
        if (userRepository.getUserById(id) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        userRepository.deleteUser(id);
    }
}
