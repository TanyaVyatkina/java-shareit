package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByBooker_IdAndStartAfter(long userId, LocalDateTime date, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByBooker_IdAndEndBefore(long userId, LocalDateTime date, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(long userId, LocalDateTime start,
                                                           LocalDateTime end, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByBooker_IdAndStatus(long userId, BookingStatus bookingStatus, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByBooker_Id(long userId, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByItem_IdIn(List<Long> itemIds, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByItem_IdInAndStatus(List<Long> itemIds, BookingStatus bookingStatus, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByItem_IdInAndStartAfter(List<Long> itemIds, LocalDateTime date, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByItem_IdInAndEndBefore(List<Long> itemIds, LocalDateTime date, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByItem_Id(Long itemId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByItem_IdIn(List<Long> itemIds);

    List<Booking> findByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long bookerId, LocalDateTime time);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByItem_IdInAndStartBeforeAndEndAfter(List<Long> itemIds, LocalDateTime start,
                                                           LocalDateTime end, Pageable pageable);

}
