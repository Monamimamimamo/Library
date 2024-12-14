package com.urfu.library.service;

import com.urfu.library.model.Reservation;
import com.urfu.library.model.Book;
import com.urfu.library.model.repository.BookRepository;
import com.urfu.library.model.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления бронированиями книг из библиотеки.
 * Предоставляет методы для создания, получения и изменения бронирований.
 */
@Service
public class ReservationService {

    private final MailerService mailerService;
    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final StatisticService statisticService;

    @Autowired
    public ReservationService(MailerService mailerService, ReservationRepository reservationRepository, BookRepository bookRepository, StatisticService statisticService) {
        this.mailerService = mailerService;
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.statisticService = statisticService;
    }

    /**
     * Срабатывает каждый день в определённое время.
     * Проверяет вышли ли невозвращённые книги за дедлайн.
     * В случае пропуска дедлайна, статус дедлайна бронирования меняется на true, и отправляется характерное сообщение пользователю и администраторам.
     * Иначе проверяется необходимость отправки напоминания о предстоящнем дедлайне.
     * Напоминание происходит, если до дедлайна осталось меньше 5 дней.
     */
    @Scheduled(cron = "${deadline.status.update.cron}")
    public void updateMissedDeadlines() {
        LocalDateTime now = LocalDateTime.now();

        List<Reservation> activeReservations = reservationRepository.findByIsReturned(false);

        for (Reservation reservation : activeReservations) {
            if (reservation.getFinishDate().isBefore(now)) {
                reservation.setDeadlineMissed(true);
                reservationRepository.save(reservation);
                mailerService.notifyDeadlineExpired(reservation);
                continue;
            }

            boolean needToNotify = reservation.getFinishDate()
                    .isBefore(now.plusDays(5));
            if (needToNotify) {
                long daysLeft = ChronoUnit.DAYS
                        .between(now.toLocalDate(), reservation.getFinishDate());
                mailerService.notifyDeadline(reservation, daysLeft);
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
     * Обновляет статистику пользователя.
     * Отправляется сообщение пользователю и администраторам.
     */
    public boolean returnBook(Long bookId) {
        Optional<Reservation> optionalReservation = reservationRepository.findByIsReturnedAndBookId(false, bookId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            if (!reservation.isReturned()) {
                statisticService.updateStatistic(reservation);
                reservation.setReturned(true);
                reservationRepository.save(reservation);

                Book book = bookRepository.findById(reservation.getBookId()).get();
                book.setReserved(false);
                bookRepository.save(book);
                mailerService.notifyReturned(reservation);
                return true;
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
