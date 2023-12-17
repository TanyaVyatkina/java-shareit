package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemService itemService;
    private Item item;

    private User owner;

    @BeforeEach
    void saveItems() {
        item = new Item();
        item.setId(1L);
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(true);

        owner = new User();
        owner.setEmail("owner@mail.ru");
        owner.setName("Никодим");
        item.setOwner(owner);
        owner = userRepository.save(owner);
        item = itemRepository.save(item);
    }

    @Test
    void testGetUserItems() {
        List<ItemDto> resultItems = itemService.getUserItems(owner.getId(), PageRequest.of(0,3));
        assertEquals(1, resultItems.size());

        ItemDto result = resultItems.get(0);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getOwner().getId(), result.getOwnerId());
    }
}
