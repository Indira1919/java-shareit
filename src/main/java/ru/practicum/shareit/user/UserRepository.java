package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer counter = 0;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User addUser(User user) {
        int id = ++counter;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public void deleteUser(Integer id) {
        users.remove(id);
    }
}
