package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    User create(User user);

    User update(User user);

    Optional<User> findById(Integer id);

    void deleteById(int id);

    Optional<User> getUserByEmail(String email);
}
