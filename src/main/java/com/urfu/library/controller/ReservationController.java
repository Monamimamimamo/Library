package com.urfu.library.controller;

import com.urfu.library.model.Reservation;
import com.urfu.library.model.User;
import com.urfu.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления операциями с бронированиями.
 * Обрабатывает HTTP-запросы, связанные с бронированиями.
 */
@RestController
@RequestMapping("/api/book/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Создаёт бронирование для книги.
     *
     * @return ResponseEntity со списком книг и статусом HTTP.
     * HttpStatus: OK, в случае успеха.
     * HttpStatus: NOT_FOUND, в случае, если книга уже забронированна или такой книги не существует.
     * HttpStatus: UNAUTHORIZED, в случае, если пользователь не авторизовался.
     */
    @PatchMapping("/{bookId}")
    public ResponseEntity<Object> reserveBook(@PathVariable Long bookId, @AuthenticationPrincipal User user) {
        Optional<Reservation> reservation = reservationService.reserveBook(bookId, user.getId());
        return reservation.isPresent()
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Возвращает книгу в библиотеку (только для Админа).
     *
     * @return ResponseEntity со списком книг и статусом HTTP.
     * HttpStatus: OK, в случае успеха.
     * HttpStatus: NOT_FOUND, в случае, если бронирования с такой книгой не существует или книга уже возвращена.
     * HttpStatus: UNAUTHORIZED, в случае, если пользователь не авторизовался.
     * HttpStatus: FORBIDDEN, в случае, если пользователь не является Админом.
     */
    @PatchMapping("/return/{bookId}")
    public ResponseEntity<Object> returnBook(@PathVariable Long bookId) {
        if (reservationService.returnBook(bookId))
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Возвращает все бронирования пользователя.
     *
     * @return ResponseEntity со списком книг и статусом HTTP.
     * HttpStatus: OK, в случае успеха.
     * HttpStatus: NO_CONTENT, в случае отсутствия активных бронирований.
     * HttpStatus: UNAUTHORIZED, в случае, если пользователь не авторизовался.
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getActiveUserReservations(@AuthenticationPrincipal User user) {
        List<Reservation> reservations = reservationService.getActiveUserReservations(user.getId());
        return reservations.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    /**
     * Возвращает все бронирования (только для Админа).
     *
     * @return ResponseEntity со списком книг и статусом HTTP.
     * HttpStatus: OK, в случае успеха.
     * HttpStatus: NO_CONTENT, в случае отсутствия активных бронирований.
     * HttpStatus: UNAUTHORIZED, в случае, если пользователь не авторизовался.
     * HttpStatus: FORBIDDEN, в случае, если пользователь не является Админом.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Reservation>> getAllActiveReservations() {
        List<Reservation> reservations = reservationService.getAllActiveReservations();
        return reservations.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(reservations, HttpStatus.OK);
    }
}
