package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {
    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void initUserService() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testFindUser_ByIdNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException result = assertThrows(NotFoundException.class,
                () -> userService.findUserById(1L));
        assertEquals(result.getMessage(), "Пользователь с заданным id не найден.");
    }

    @Test
    void testFindUser() {
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

    @Test
    void testCreateUser() {
        when(userRepository.save(any())).thenReturn(new User());
        userService.createUser(new UserDto(null, "Иванов Иван", "ii@mail.ru"));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testUpdateUser_NotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException result = assertThrows(NotFoundException.class,
                () -> userService.updateUser(1L, new UserDto(null, "Иванов Иван", "ii@mail.ru")));
        assertEquals(result.getMessage(), "Пользователь с id = 1 не найден.");
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        User user = new User();
        user.setId(1L);
        user.setName("Иванов Иван");
        user.setEmail("ii@mail.ru");
        when(userRepository.save(any())).thenReturn(user);
        UserDto userDto = new UserDto(null, "Иванов Иван", "ii@mail.ru");
        UserDto result = userService.updateUser(1L, userDto);
        verify(userRepository, times(1)).save(any());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getName(), result.getName());
    }

    @Test
    void testDeleteUserById() {
        userService.deleteUserById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
