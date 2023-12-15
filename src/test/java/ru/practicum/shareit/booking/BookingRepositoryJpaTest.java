package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    private User booker;
    private Item item;
    private PageRequest page = PageRequest.of(0, 3).withSort(Sort.Direction.DESC, "id");


    @BeforeEach
    void saveData() {
        booker = new User();
        booker.setName("Петя Иванов");
        booker.setEmail("petyaTheBest@yandex.ru");
        em.persist(booker);

        User owner = new User();
        owner.setName("Саша Петров");
        owner.setEmail("zvezda@yandex.ru");
        em.persist(owner);

        item = new Item();
        item.setName("Лыжи детские");
        item.setDescription("Длина 120 см");
        item.setOwner(owner);
        item.setAvailable(true);
        em.persist(item);
        em.flush();
    }

    @Test
    void testFindByBooker_IdAndStartBeforeAndEndAfter() {
        Booking current = new Booking();
        current.setItem(item);
        current.setBooker(booker);
        current.setStatus(BookingStatus.APPROVED);
        current.setStart(LocalDateTime.now().minusDays(5));
        current.setEnd(LocalDateTime.now().plusDays(2));
        em.persist(current);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(booker.getId(),
                LocalDateTime.now(), LocalDateTime.now(), page);
        assertEquals(1, resultBookings.size());
    }

    @Test
    void testFindByBooker_IdAndEndBefore() {
        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        em.persist(pastBooking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByBooker_IdAndEndBefore(booker.getId(),
                LocalDateTime.now(), page);
        assertEquals(1, resultBookings.size());
    }

    @Test
    void testFindByBooker_IdAndStartAfter() {
        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.APPROVED);
        futureBooking.setStart(LocalDateTime.now().plusDays(3));
        futureBooking.setEnd(LocalDateTime.now().plusDays(4));
        em.persist(futureBooking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByBooker_IdAndStartAfter(booker.getId(),
                LocalDateTime.now(), page);
        assertEquals(1, resultBookings.size());
    }

    @Test
    void testFindByBooker_IdAndStatus() {
        Booking rejectedBooking = new Booking();
        rejectedBooking.setItem(item);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        rejectedBooking.setStart(LocalDateTime.now().plusDays(3));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(4));
        em.persist(rejectedBooking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByBooker_IdAndStatus(booker.getId(),
                BookingStatus.REJECTED, page);
        assertEquals(1, resultBookings.size());
    }

    @Test
    void testFindByBooker_Id() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(4));
        em.persist(booking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByBooker_Id(booker.getId(), page);
        assertEquals(1, resultBookings.size());
    }

    @Test
    void testFindByItem_Id() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(4));
        em.persist(booking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByItem_Id(item.getId());
        assertEquals(1, resultBookings.size());
    }

    @Test
    void testFindByItem_IdInAndEndBefore() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(4));
        em.persist(booking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByItem_IdInAndEndBefore(List.of(item.getId()),
                LocalDateTime.now(), page);
        assertEquals(1, resultBookings.size());
    }

    @Test
    void testFindByItem_IdInAndStartAfter() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(4));
        em.persist(booking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByItem_IdInAndStartAfter(List.of(item.getId()),
                LocalDateTime.now(), page);
        assertEquals(1, resultBookings.size());
    }

    @Test
    void testFindByItem_IdInAndStatus() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(4));
        em.persist(booking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByItem_IdInAndStatus(List.of(item.getId()),
                BookingStatus.WAITING, page);
        assertEquals(1, resultBookings.size());
    }

    @Test
    void testFindByItem_IdIn() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(4));
        em.persist(booking);
        em.flush();

        List<Booking> resultBookings = bookingRepository.findByItem_IdIn(List.of(item.getId()), page);
        assertEquals(1, resultBookings.size());
    }

    @AfterEach
    void deleteData() {
        bookingRepository.deleteAll();
    }
}
