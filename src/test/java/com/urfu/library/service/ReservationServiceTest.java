package com.urfu.library.service;

import com.urfu.library.model.Book;
import com.urfu.library.model.Reservation;
import com.urfu.library.model.repository.BookRepository;
import com.urfu.library.model.repository.ReservationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Класс реализует модульные тесты для сервиса бронирований
 */
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
    private Book book;

    /**
     * Метод инициализации для каждого теста.
     * Создаёт тестовые экземпляры книги и бронирования.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        book = new Book();
        book.setId(1L);
        book.setReserved(false);

        reservation = new Reservation();
        reservation.setBookId(1L);
        reservation.setUserId(10L);
        reservation.setReturned(false);
        reservation.setFinishDate(LocalDateTime.now().plusDays(1));
    }

    /**
     * Тестирует успешное бронирование книги пользователем.
     * В случае успешного бронирования книга помечается как забронированная,
     * а метод reserveBook возвращает объект бронирования.
     */
    @Test
    void testReserveBook_Success() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(reservationRepository.save(Mockito.any(Reservation.class))).thenReturn(reservation);

        Optional<Reservation> result = reservationService.reserveBook(1L, 10L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(reservation, result.get());
        Assertions.assertTrue(book.isReserved());

        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    /**
     * Тестирует сценарий, когда книга не найдена в БД.
     * В случае, если книга не найдена, метод reserveBook возвращает Optional.empty()
     */
    @Test
    void testReserveBook_BookNotFound() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Reservation> result = reservationService.reserveBook(1L, 10L);

        Assertions.assertTrue(result.isEmpty());
    }


    /**
     * Тестирует сценарий, когда книга уже забронирована.
     * В этом случае метод reserveBook возвращает Optional.empty(),
     * так как книгу нельзя забронировать повторно.
     */
    @Test
    void testReserveBook_BookAlreadyReserved() {
        book.setReserved(true);
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Reservation> result = reservationService.reserveBook(1L, 10L);

        Assertions.assertTrue(result.isEmpty());
    }

    /**
     * Тестирует успешное возвращение книги в библиотеку.
     * Когда книга успешно возвращена, её статус в бронировании обновляется,
     * а книга помечается как доступная для бронирования.
     */
    @Test
    void testReturnBook_Success() {
        Mockito.when(reservationRepository.findByIsReturnedAndBookId(false, 1L)).thenReturn(Optional.of(reservation));
        Mockito.when(bookRepository.getById(1L)).thenReturn(book);

        boolean result = reservationService.returnBook(1L);

        Assertions.assertTrue(result);
        Assertions.assertTrue(reservation.isReturned());
        Assertions.assertFalse(book.isReserved());

        Mockito.verify(reservationRepository, Mockito.times(1)).save(reservation);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    /**
     * Тестирует сценарий, когда нет активного бронирования для указанной книги.
     * В этом случае метод returnBook возвращает false, так как возврат невозможен.
     */
    @Test
    void testReturnBook_NoReservation() {
        Mockito.when(reservationRepository.findByIsReturnedAndBookId(false, 1L)).thenReturn(Optional.empty());

        boolean result = reservationService.returnBook(1L);

        Assertions.assertFalse(result);
    }

    /**
     * Тестирует получение списка активных бронирований для конкретного пользователя.
     * В случае наличия активных бронирований, метод getActiveUserReservations
     * возвращает непустой список бронирований.
     */
    @Test
    void testGetActiveUserReservations() {
        Mockito.when(reservationRepository.findByUserIdAndIsReturned(10L, false)).thenReturn(List.of(reservation));

        var reservations = reservationService.getActiveUserReservations(10L);

        Assertions.assertNotNull(reservations);
        Assertions.assertEquals(1, reservations.size());
        Assertions.assertEquals(reservation, reservations.get(0));
    }

    /**
     * Тестирует получение списка всех активных бронирований.
     * Метод getAllActiveReservations должен возвращать список всех активных бронирований.
     */
    @Test
    void testGetAllActiveReservations() {
        Mockito.when(reservationRepository.findByIsReturned(false)).thenReturn(List.of(reservation));

        var reservations = reservationService.getAllActiveReservations();

        Assertions.assertNotNull(reservations);
        Assertions.assertEquals(1, reservations.size());
        Assertions.assertEquals(reservation, reservations.get(0));
    }

    /**
     * Тестирует выполнение задачи для обновления статуса дедлайнов для книг, которые не были возвращены в срок.
     * В случае пропуска дедлайна, статус isDeadlineMissed изменяется на true.
     * В первый раз статус не должен меняться, из-за чего мы ожидаем save() 1 раз.
     */
    @Test
    void testUpdateMissedDeadlines() {
        reservation.setFinishDate(LocalDateTime.now().plusDays(1));
        Mockito.when(reservationRepository.findByIsReturned(false)).thenReturn(List.of(reservation));
        reservationService.updateMissedDeadlines();

        Assertions.assertFalse(reservation.isDeadlineMissed());

        reservation.setFinishDate(LocalDateTime.now().minusDays(1));
        Mockito.when(reservationRepository.findByIsReturned(false)).thenReturn(List.of(reservation));
        reservationService.updateMissedDeadlines();

        Assertions.assertTrue(reservation.isDeadlineMissed());

        Mockito.verify(reservationRepository, Mockito.times(1)).save(reservation);
    }

}
