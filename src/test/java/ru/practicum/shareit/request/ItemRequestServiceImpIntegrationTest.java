package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImpIntegrationTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void saveRequests() {
        user = new User();
        user.setId(1L);
        user.setName("Петр Петрович");
        user.setEmail("email@email.com");
        user = userRepository.save(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Самовар найдись.");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
    }
    @Test
    void testGetUserItemRequests() {
        List<ItemRequestDto> resultRequests = itemRequestService.getUserItemRequests(user.getId());

        assertEquals(resultRequests.size(), 1);
        ItemRequestDto result = resultRequests.get(0);
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
    }
}
