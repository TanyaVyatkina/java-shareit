package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers();

    UserDto findUserById(Integer id);

    UserDto createUser(UserDto user);

    UserDto updateUser(Integer id, UserDto user);

    void deleteUserById(Integer userId);

}
