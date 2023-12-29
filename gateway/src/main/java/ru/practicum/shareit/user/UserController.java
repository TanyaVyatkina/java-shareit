package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity findAll() {
        log.debug("Поиск всех пользователей.");
        return userClient.getAllUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") Long id) {
        log.debug("Поиск пользователя с id = {}.", id);
        ResponseEntity user = userClient.getUser(id);
        log.debug("Найден пользователь {}.", id);
        return user;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Validated({OnCreate.class}) UserDto user) {
        log.debug("Пришел запрос на добавление пользователя.");
        ResponseEntity createdUser = userClient.saveUser(user);
        log.debug("Пользователь добавлен.");
        return createdUser;

    }

    @PatchMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody @Validated({OnUpdate.class}) UserDto user) {
        log.debug("Пришел запрос на обновление пользователя.");
        ResponseEntity updatedUser = userClient.updateUser(id, user);
        log.debug("Обновлен пользователь с id = {}", user.getId());
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        userClient.deleteUser(id);
        log.debug("Пользователь с id = {} удалён", id);
    }
}
