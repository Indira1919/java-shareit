package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerId(Integer booker, Sort sort);

    List<Booking> findAllByBooker(User booker, Pageable pageable);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start,
                                                           LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerAndStatusEquals(User booker, Status status, Pageable pageable);

    List<Booking> findAllByItemOwner(User owner, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start,
                                                              LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, Status status, Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStartLessThanEqualAndStatus(Integer itemId, LocalDateTime now,
                                                                      Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusEquals(Integer itemId, LocalDateTime now,
                                                                    Status status, Sort sort);
}
