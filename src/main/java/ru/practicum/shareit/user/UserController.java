package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.debug("Поиск всех пользователей.");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") Integer id) {
        log.debug("Поиск пользователя с id = {}.", id);
        UserDto user = userService.findUserById(id);
        log.debug("Найден пользователь {}.", id);
        return user;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto user) {
        log.debug("Пришел запрос на добавление пользователя.");
        UserDto createdUser = userService.createUser(user);
        log.debug("Добавлен пользователь c id = {}", createdUser.getId());
        return createdUser;

    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Integer id, @RequestBody UserDto user) {
        log.debug("Пришел запрос на обновление пользователя.");
        UserDto updatedUser = userService.updateUser(id, user);
        log.debug("Обновлен пользователь с id = {}", user.getId());
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Integer id) {
        userService.deleteUserById(id);
        log.debug("Пользователь с id = {} удалён", id);
    }

}
