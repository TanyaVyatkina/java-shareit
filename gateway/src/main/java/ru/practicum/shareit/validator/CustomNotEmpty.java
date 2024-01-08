package ru.practicum.shareit.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = CustomNotEmptyValidator.class)
public @interface CustomNotEmpty {
    String message() default "{Поле должно быть заполнено.}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}