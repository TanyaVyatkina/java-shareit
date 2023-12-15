package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User requestor;
    PageRequest page = PageRequest.of(0, 3);


    @BeforeEach
    void saveData() {
        requestor = new User();
        requestor.setName("Петя Иванов");
        requestor.setEmail("petyaTheBest@yandex.ru");
        em.persist(requestor);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужна малиновая девятка");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        em.persist(itemRequest);
        em.flush();
    }

    @Test
    void testFindByRequestor_Id() {
        List<ItemRequest> resultRequests = itemRequestRepository.findByRequestor_Id(requestor.getId());
        assertEquals(1, resultRequests.size());
    }

    @Test
    void testFindByRequestor_IdNot_NotFound() {
        List<ItemRequest> resultRequests = itemRequestRepository.findByRequestor_IdNot(requestor.getId(), page);
        assertEquals(0, resultRequests.size());
    }

    @Test
    void testFindByRequestor_IdNot_FoundOne() {
        User user = new User();
        user.setName("Cтепан");
        user.setEmail("stepashka@yandex.ru");
        em.persist(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Моющий пылесос");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        em.persist(itemRequest);
        em.flush();

        List<ItemRequest> resultRequests = itemRequestRepository.findByRequestor_IdNot(requestor.getId(), page);
        assertEquals(1, resultRequests.size());
    }
}
