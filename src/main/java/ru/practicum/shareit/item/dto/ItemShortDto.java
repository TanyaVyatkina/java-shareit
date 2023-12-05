package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validator.CustomNotEmpty;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ItemShortDto {
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "Имя должно быть заполнено.")
    @CustomNotEmpty(groups = OnUpdate.class, message = "Имя должно быть заполнено.")
    private String name;
    @NotBlank(groups = OnCreate.class, message = "Описание должно быть заполнено.")
    @CustomNotEmpty(groups = OnUpdate.class, message = "Описание должно быть заполнено.")
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    private Long ownerId;
}
