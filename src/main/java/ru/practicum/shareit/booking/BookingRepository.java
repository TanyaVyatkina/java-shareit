package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndStartAfterOrderByIdDesc(long userId, LocalDateTime date);

    List<Booking> findByBooker_IdAndEndBeforeOrderByIdDesc(long userId, LocalDateTime date);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByIdAsc(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndStatusOrderByIdDesc(long userId, BookingStatus bookingStatus);

    List<Booking> findByBooker_IdOrderByIdDesc(long userId);

    List<Booking> findByItem_IdInOrderByIdDesc(List<Long> itemIds);

    List<Booking> findByItem_IdInAndStatusOrderByIdDesc(List<Long> itemIds, BookingStatus bookingStatus);

    List<Booking> findByItem_IdInAndStartAfterOrderByIdDesc(List<Long> itemIds, LocalDateTime date);

    List<Booking> findByItem_IdInAndEndBeforeOrderByIdDesc(List<Long> itemIds, LocalDateTime date);

    List<Booking> findByItem_Id(Long itemId);

    List<Booking> findByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long bookerId, LocalDateTime time);

    List<Booking> findByItem_IdInAndStartBeforeAndEndAfterOrderByIdDesc(List<Long> itemIds, LocalDateTime start, LocalDateTime end);

}
