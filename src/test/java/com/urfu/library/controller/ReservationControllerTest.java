package com.urfu.library.controller;

import com.urfu.library.model.Book;
import com.urfu.library.model.Reservation;
import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import com.urfu.library.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс реализует модульные тесты для контроллера бронирований
 */
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;
    private User user;
    private Long bookId;
    private List<Reservation> emptyReservations;
    private List<Reservation> reservations;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();

        emptyReservations = new ArrayList<>();

        reservations = new ArrayList<>();
        reservation = new Reservation();
        reservations.add(reservation);

        bookId = 1L;

        user = new User("kirill", "ugabuga@yandex.ru", "12345678", Role.ROLE_USER);
    }

    /**
     * Тестирует успешное бронирование книги.
     * Ожидается, что вернётся статус 200 OK.
     */
    @Test
    void reserveBook_Success() throws Exception {
        Mockito.when(reservationService.reserveBook(bookId, user.getId())).thenReturn(Optional.of(reservation));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/book/reservation/{bookId}", bookId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(reservationService, Mockito.times(1)).reserveBook(bookId, user.getId());
    }

    /**
     * Тестирует бронирование, если книга не найдена.
     * Ожидается, что вернётся статус 404 Not Found.
     */
    @Test
    void reserveBook_NotFound() throws Exception {
        Mockito.when(reservationService.reserveBook(bookId, user.getId())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/book/reservation/{bookId}", bookId)
                        .principal(() -> user.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(reservationService, Mockito.times(1)).reserveBook(bookId, user.getId());
    }

    /**
     * Тестирует успешный возврат книги.
     * Ожидается, что вернётся статус 200 OK.
     */
    @Test
    void returnBook_Success() throws Exception {
        Mockito.when(reservationService.returnBook(bookId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/book/reservation/return/{bookId}", bookId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(reservationService, Mockito.times(1)).returnBook(bookId);
    }

    /**
     * Тестирует отсутствие нужного бронирования при возвращении книги.
     * Ожидается, что вернётся статус 404 Not Found.
     */
    @Test
    void returnBook_NotFound() throws Exception {
        Mockito.when(reservationService.returnBook(bookId)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/book/reservation/return/{bookId}", bookId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(reservationService, Mockito.times(1)).returnBook(bookId);
    }

    /**
     * Тестирует успешный возврат списка активных бронирований конкретного пользователя.
     * Ожидается, что вернётся статус 200 OK.
     */
    @Test
    void getActiveUserReservations_Success() throws Exception {
        Mockito.when(reservationService.getActiveUserReservations(user.getId())).thenReturn(reservations);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/reservation")
                        .principal(() -> user.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());

        Mockito.verify(reservationService, Mockito.times(1)).getActiveUserReservations(user.getId());
    }

    /**
     * Тестирует попытку получить бронирования при отсутствии их у пользователя.
     * Ожидается, что вернётся статус 204 No Content.
     */
    @Test
    void getActiveUserReservations_NoContent() throws Exception {
        Mockito.when(reservationService.getActiveUserReservations(user.getId())).thenReturn(emptyReservations);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/reservation")
                        .principal(() -> user.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(reservationService, Mockito.times(1)).getActiveUserReservations(user.getId());
    }

    /**
     * Тестирует успешный возврат списка активных бронирований.
     * Ожидается, что вернётся статус 200 OK.
     */
    @Test
    void getAllActiveReservations_Success() throws Exception {

        Mockito.when(reservationService.getAllActiveReservations()).thenReturn(reservations);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/reservation/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());

        Mockito.verify(reservationService, Mockito.times(1)).getAllActiveReservations();
    }

    /**
     * Тестирует попытку получить бронирования при отсутствии их в БД.
     * Ожидается, что вернётся статус 204 No Content.
     */
    @Test
    void getAllActiveReservations_NoContent() throws Exception {
        Mockito.when(reservationService.getAllActiveReservations()).thenReturn(emptyReservations);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/reservation/all"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(reservationService, Mockito.times(1)).getAllActiveReservations();
    }
}
