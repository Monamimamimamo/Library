package com.urfu.library.service;

import com.urfu.library.model.Reservation;
import com.urfu.library.model.Book;
import com.urfu.library.model.repository.BookRepository;
import com.urfu.library.model.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления бронированиями книг из библиотеки.
 * Предоставляет методы для создания, получения и изменения бронирований.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Каждый день в определённое время проверяет вышли ли невозвращённые книги за дедлайн.
     * В случае пропуска дедлайна, статус дедлайна бронирования меняется на true.
     */
    @Scheduled(cron = "${deadline.status.update.cron}")
    public void updateMissedDeadlines() {
        LocalDateTime now = LocalDateTime.now();

        List<Reservation> activeReservations = reservationRepository.findByIsReturned(false);

        for (Reservation reservation : activeReservations) {
            if (reservation.getFinishDate().isBefore(now)) {
                reservation.setDeadlineMissed(true);
                reservationRepository.save(reservation);
            }
        }
    }

    /**
     * Создание бронирования на книгу.
     * Возвращает Optional.empty() в случае отсутствия книги и в случае,
     * если книга уже забронирована, иначе Optional<Reservation>.
     * Изменяет isReserved книги на true.
     */
    public Optional<Reservation> reserveBook(Long bookId, Long userId) {
        Optional<Book> existingBook = bookRepository.findById(bookId);
        if (existingBook.isEmpty() || existingBook.get().isReserved()) {
            return Optional.empty();
        }
        Book book = existingBook.get();

        Reservation reservation = new Reservation(book.getId(), userId, false, false, LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        reservation = reservationRepository.save(reservation);

        book.setReserved(true);
        bookRepository.save(book);

        return Optional.of(reservation);
    }

    /**
     * Возвращение книги в библиотеку по её Id.
     * Возвращает false, в случае отсутствия активного бронирования по данной книге, иначе true.
     * Бронирование считается неактивным после возврата книги.
     * Изменяет isReserved книги на false.
     */
    public boolean returnBook(Long bookId) {
        Optional<Reservation> optionalReservation = reservationRepository.findByIsReturnedAndBookId(false, bookId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            if (!reservation.isReturned()) {
                reservation.setReturned(true);
                reservationRepository.save(reservation);

                Optional<Book> optionalBook = bookRepository.findById(reservation.getBookId());
                if (optionalBook.isPresent()) {
                    Book book = optionalBook.get();
                    book.setReserved(false);
                    bookRepository.save(book);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Получение списка активных бронирований конкретного пользователя
     * или пустого списка, если активных бронирований нет.
     */
    public List<Reservation> getActiveUserReservations(Long userId) {
        return reservationRepository.findByUserIdAndIsReturned(userId, false);
    }

    /**
     * Получение списка активных бронирований всех пользователей
     * или пустого списка, если активных бронирований нет.
     */
    public List<Reservation> getAllActiveReservations() {
        return reservationRepository.findByIsReturned(false);
    }
}
