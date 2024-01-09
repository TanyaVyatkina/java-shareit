package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner_Id(long userId);

    List<Item> findByOwner_Id(long userId, Pageable pageable);

    List<Item> findByItemRequest_Id(long itemRequestId);

    List<Item> findByItemRequest_IdIn(List<Long> requestsIds);


    @Query("select it " +
            "from Item as it " +
            "where it.available = true and (lower(it.name) like lower(concat('%', ?1,'%')) " +
            "or lower(it.description) like lower(concat('%', ?1,'%')))")
    List<Item> findItemsByText(String text, Pageable pageable);

    Optional<Item> findByIdAndOwner_IdIsNot(long itemId, long userId);

    Optional<Item> findByIdAndOwner_Id(long itemId, long userId);
}
