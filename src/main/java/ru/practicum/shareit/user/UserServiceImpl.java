package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDtoList(users);
    }

    @Override
    public UserDto findUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с заданным id не найден;"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto.getEmail(), null);
        User user = userRepository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        User user = findUserIfExist(id);
        String userEmail = userDto.getEmail();

        validateEmail(userEmail, id);
        if (userEmail != null) {
            user.setEmail(userEmail);
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        userRepository.update(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    private User findUserIfExist(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден."));
    }

    private void validateEmail(String email, Integer userId) {
        if (email == null) return;
        if (email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Некорректный email.");
        }
        Optional<User> savedUser = userRepository.getUserByEmail(email);
        if (userId == null && savedUser.isPresent()
                || userId != null && savedUser.isPresent() && !userId.equals(savedUser.get().getId())) {
            throw new ConflictEmailException("Указанный email уже существует.");
        }
    }
}
