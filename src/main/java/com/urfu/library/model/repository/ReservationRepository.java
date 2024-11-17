package com.urfu.library.model.repository;

import com.urfu.library.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий сущности Reservation для взаимодействия с базой данных
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Поиск всех завершенных бронирований, если передан true,
     * иначе ищутся все незавершенные бронирования
     */
    List<Reservation> findByIsReturned(boolean isReturned);

    /**
     * По аналогии с findByIsReturned, но для конкретного пользователя
     */
    List<Reservation> findByUserIdAndIsReturned(Long userId, boolean isReturned);

    /**
     * Поиск бронирования по Id зафиксированноё в нём книги, для конкретного пользователя
     */
    Optional<Reservation> findByIsReturnedAndBookId(boolean isReturned, Long bookId);
}
