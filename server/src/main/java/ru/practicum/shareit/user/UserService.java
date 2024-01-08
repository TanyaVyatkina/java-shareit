package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers();

    UserDto findUserById(Long id);

    UserDto createUser(UserDto user);

    UserDto updateUser(Long id, UserDto user);

    void deleteUserById(Long userId);

}
