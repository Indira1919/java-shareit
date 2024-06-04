package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAll(Integer page, Integer size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")))
                .stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()
                && !userDto.getEmail().equals(user.getEmail())) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        userRepository.deleteById(id);
    }
}
