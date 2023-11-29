package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.validator.CustomNotEmpty;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class
ItemDto {
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "Имя должно быть заполнено.")
    @CustomNotEmpty(groups = OnUpdate.class, message = "Имя должно быть заполнено.")
    private String name;
    @NotBlank(groups = OnCreate.class, message = "Описание должно быть заполнено.")
    @CustomNotEmpty(groups = OnUpdate.class, message = "Описание должно быть заполнено.")
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    private Integer ownerId;
   // private ItemRequest request;

    public ItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
