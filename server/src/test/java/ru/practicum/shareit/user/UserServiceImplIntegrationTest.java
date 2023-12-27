package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void saveUsers() {
        user = new User();
        user.setId(1L);
        user.setName("Петр Петрович");
        user.setEmail("email@email.com");
        userRepository.save(user);
    }

    @Test
    void testFindAllUsers() {
        List<UserDto> resultUsers = userService.findAllUsers();

        assertEquals(resultUsers.size(), 1);
        UserDto result = resultUsers.get(0);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }
}
