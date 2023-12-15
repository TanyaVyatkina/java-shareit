package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testFindItemsByTextByName() {
        User owner = new User();
        owner.setName("Петя Иванов");
        owner.setEmail("petyaTheBest@yandex.ru");
        em.persist(owner);

        Item item = new Item();
        item.setName("Лыжи детские");
        item.setDescription("Длина 120 см");
        item.setOwner(owner);
        item.setAvailable(true);
        em.persist(item);
        em.flush();

        PageRequest page = PageRequest.of(0, 1);
        List<Item> resultItems = itemRepository.findItemsByText("лыжи", page);

        assertEquals(resultItems.size(), 1);
        Item resultItem = resultItems.get(0);
        assertEquals(resultItem.getName(), item.getName());
        assertEquals(resultItem.getDescription(), item.getDescription());
        assertEquals(resultItem.getAvailable(), item.getAvailable());
        assertEquals(resultItem.getOwner(), item.getOwner());
    }

    @Test
    void testFindItemsByTextByDescription() {
        User owner = new User();
        owner.setName("Петя Иванов");
        owner.setEmail("petyaTheBest@yandex.ru");
        em.persist(owner);

        Item item = new Item();
        item.setName("Коньки");
        item.setDescription("Роликовые коньки. 36 размер");
        item.setOwner(owner);
        item.setAvailable(true);
        em.persist(item);
        em.flush();
        PageRequest page = PageRequest.of(0, 1);
        List<Item> resultItems = itemRepository.findItemsByText("рол", page);

        assertEquals(resultItems.size(), 1);
        Item resultItem = resultItems.get(0);
        assertEquals(resultItem.getName(), item.getName());
        assertEquals(resultItem.getDescription(), item.getDescription());
        assertEquals(resultItem.getAvailable(), item.getAvailable());
        assertEquals(resultItem.getOwner(), item.getOwner());
    }

    @Test
    void testFindItemsByTextWithNoFound() {
        User owner = new User();
        owner.setName("Петя Иванов");
        owner.setEmail("petyaTheBest@yandex.ru");
        em.persist(owner);

        Item item = new Item();
        item.setName("Лыжи детские");
        item.setDescription("Длина 120 см");
        item.setOwner(owner);
        item.setAvailable(true);
        em.persist(item);
        em.flush();

        PageRequest page = PageRequest.of(0, 1);
        List<Item> resultItems = itemRepository.findItemsByText("дрель", page);

        assertEquals(resultItems.size(), 0);
    }
}
