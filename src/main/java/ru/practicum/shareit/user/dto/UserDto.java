package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validator.CustomNotEmpty;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class
UserDto {
    private Integer id;
    @NotBlank(groups = OnCreate.class, message = "Имя должно быть заполнено.")
    @CustomNotEmpty(groups = OnUpdate.class, message = "Имя должно быть заполнено.")
    private String name;
    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "Некорректный email.")
    @NotBlank(groups = OnCreate.class, message = "Некорректный email.")
    private String email;
}
