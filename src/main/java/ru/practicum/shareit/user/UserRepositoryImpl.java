package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;

import java.util.*;

@Component
public class UserRepositoryImpl {
   /* private Map<Integer, User> usersById = new HashMap<>();
    private Map<String, User> usersByEmail = new HashMap<>();
    private int userId = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public User create(User user) {
        if (usersByEmail.keySet().contains(user.getEmail())) {
            throw new ConflictException("Указанный email уже существует.");
        }
        Integer newId = getUserId();
        user.setId(newId);
        usersById.put(newId, user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User update(User user) {
        Integer userId = user.getId();
        String newEmail = user.getEmail();
        User userToUpdate = usersById.get(userId);
        if (newEmail != null) {
            User userWithNewEmail = usersByEmail.get(newEmail);
            if (userWithNewEmail != null
                    && !userId.equals(userWithNewEmail.getId())) {
                throw new ConflictException("Указанный email уже существует.");
            }
            usersByEmail.remove(userToUpdate.getEmail());
            userToUpdate.setEmail(newEmail);
            usersByEmail.put(newEmail, userToUpdate);
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        return userToUpdate;
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public void deleteById(int id) {
        User user = findById(id).get();
        usersByEmail.remove(user.getEmail());
        usersById.remove(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    private int getUserId() {
        return ++userId;
    }*/
}
