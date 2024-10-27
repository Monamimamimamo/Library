package com.urfu.library.controller;

import com.urfu.library.model.Book;
import com.urfu.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;
    private UUID bookId;
    private Book book;



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();

        bookId = UUID.randomUUID();
        book = new Book();
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setDescription("Test Description");
    }

    /**
     * Тестирует успешное получение всех книг.
     * Что вернётся статус 200 OK.
     */
    @Test
    public void testGetAllBooks_Success() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(book));

        mockMvc.perform(get("/api/book/all", bookId))
                .andExpect(status().isOk());
        verify(bookService, times(1)).getAllBooks();
    }

    /**
     * Тестирует запрос при отсутствии книг в БД.
     * Что вернётся статус 204 OK.
     */
    @Test
    public void testGetAllBooks_NotFound() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of());

        mockMvc.perform(get("/api/book/all", bookId))
                .andExpect(status().isNoContent());
        verify(bookService, times(1)).getAllBooks();
    }

    /**
     * Тестирует успешное обновление информации о книге.
     * Ожидается, что при корректных данных будет возвращен статус 200 OK.
     */
    @Test
    public void testUpdateBookInfo_Success() throws Exception {
        when(bookService.updateBookInfo(any(UUID.class), any(Book.class)))
                .thenReturn(Optional.of(book));

        mockMvc.perform(put("/api/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Updated Title\", \"author\": \"Updated Author\", \"description\": \"Updated Description\" }"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).updateBookInfo(any(UUID.class), any(Book.class));
    }

    /**
     * Тестирует обработку некорректных данных для обновления книги.
     * Ожидается, что при отсутствии обязательных данных будет возвращен статус 422 Unprocessable Entity.
     */
    @Test
    public void testUpdateBookInfo_UnprocessableEntity() throws Exception {
        mockMvc.perform(put("/api/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": null, \"author\": \"Updated Author\", \"description\": \"Updated Description\" }"))
                .andExpect(status().isUnprocessableEntity());

        verify(bookService, never()).updateBookInfo(any(UUID.class), any(Book.class));
    }

    /**
     * Тестирует сценарий, когда книга с указанным идентификатором не найдена.
     * Этот тест проверяет, что при отсутствии книги в БД для обновления будет возвращен статус 204 No Content.
     */
    @Test
    public void testUpdateBookInfo_NotFound() throws Exception {
        when(bookService.updateBookInfo(any(UUID.class), any(Book.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Updated Title\", \"author\": \"Updated Author\", \"description\": \"Updated Description\" }"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).updateBookInfo(any(UUID.class), any(Book.class));
    }

    /**
     * Тестирует успешное удаление книги.
     * Ожидается, что при успешном удалении будет возвращен статус 200 OK.
     */
    @Test
    public void testDeleteBook_Success() throws Exception {
        when(bookService.deleteBook(bookId)).thenReturn(true);

        mockMvc.perform(delete("/api/book/{bookId}", bookId))
                .andExpect(status().isOk());

        verify(bookService, times(1)).deleteBook(bookId);
    }

    /**
     * Тестирует сценарий, когда книга для удаления не найдена.
     * будет возвращен статус 204 No Content.
     */
    @Test
    public void testDeleteBook_NotFound() throws Exception {
        when(bookService.deleteBook(bookId)).thenReturn(false);

        mockMvc.perform(delete("/api/book/{bookId}", bookId))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(bookId);
    }
}
