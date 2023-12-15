package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {
    @Mock
    private UserRepository userRepository;

    @Test
    void testFindUser_ByIdNotFoundException() {
        UserService userService = new UserServiceImpl(userRepository);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException result = assertThrows(NotFoundException.class,
                () -> userService.findUserById(1L));
        assertEquals(result.getMessage(), "Пользователь с заданным id не найден.");
    }

    @Test
    void testFindUser() {
        UserService userService = new UserServiceImpl(userRepository);
        User user = new User();
        user.setId(1L);
        user.setName("Петр Петрович");
        user.setEmail("email@email.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto result = userService.findUserById(1L);
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getName(), user.getName());
        assertEquals(result.getEmail(), user.getEmail());
    }
}
