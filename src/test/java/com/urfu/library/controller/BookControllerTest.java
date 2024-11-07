package com.urfu.library.controller;

import com.urfu.library.controller.advice.BookControllerAdvice;
import com.urfu.library.model.Book;
import com.urfu.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

/**
 * Класс реализует модульные тесты для контроллера книг
 */
public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;
    private Long bookId;
    private Book book;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).setControllerAdvice(BookControllerAdvice.class).build();

        bookId = 1L;
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
        Mockito.when(bookService.getAllBooks()).thenReturn(Arrays.asList(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/all", bookId))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(bookService, Mockito.times(1)).getAllBooks();
    }

    /**
     * Тестирует запрос при отсутствии книг в БД.
     * Что вернётся статус 204 OK.
     */
    @Test
    public void testGetAllBooks_NotFound() throws Exception {
        Mockito.when(bookService.getAllBooks()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/all", bookId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(bookService, Mockito.times(1)).getAllBooks();
    }

    /**
     * Тестирует успешное обновление информации о книге.
     * Ожидается, что при корректных данных будет возвращен статус 200 OK.
     */
    @Test
    public void testUpdateBookInfo_Success() throws Exception {
        Mockito.when(bookService.updateBookInfo(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Book.class)))
                .thenReturn(Optional.of(book));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Updated Title\", \"author\": \"Updated Author\", \"description\": \"Updated Description\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookService, Mockito.times(1)).updateBookInfo(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Book.class));
    }

    /**
     * Тестирует обработку некорректных данных для обновления книги.
     * Ожидается, что при отсутствии обязательных данных будет возвращен статус 422 Unprocessable Entity.
     */
    @Test
    public void testUpdateBookInfo_UnprocessableEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": null, \"author\": \"Updated Author\", \"description\": \"Updated Description\" }"))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        Mockito.verify(bookService, Mockito.never()).updateBookInfo(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Book.class));
    }

    /**
     * Тестирует сценарий, когда книга с указанным идентификатором не найдена.
     * Этот тест проверяет, что при отсутствии книги в БД для обновления будет возвращен статус 204 No Content.
     */
    @Test
    public void testUpdateBookInfo_NotFound() throws Exception {
        Mockito.when(bookService.updateBookInfo(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Book.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Updated Title\", \"author\": \"Updated Author\", \"description\": \"Updated Description\" }"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(bookService, Mockito.times(1)).updateBookInfo(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Book.class));
    }

    /**
     * Тестирует успешное удаление книги.
     * Ожидается, что при успешном удалении будет возвращен статус 200 OK.
     */
    @Test
    public void testDeleteBook_Success() throws Exception {
        Mockito.when(bookService.deleteBook(bookId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/book/{bookId}", bookId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookService, Mockito.times(1)).deleteBook(bookId);
    }

    /**
     * Тестирует сценарий, когда книга для удаления не найдена.
     * будет возвращен статус 204 No Content.
     */
    @Test
    public void testDeleteBook_NotFound() throws Exception {
        Mockito.when(bookService.deleteBook(bookId)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/book/{bookId}", bookId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(bookService, Mockito.times(1)).deleteBook(bookId);
    }

    /**
     * Тестирует успешное добавление новой книги.
     * Ожидает возвращение статуса 200 OK
     * @author Alexandr Filatov
     */
    @Test
    public void testSaveBook_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/book").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"Test Title\", \"author\": \"Test Author\", \"description\": \"Test Description\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(bookService, Mockito.times(1)).saveBook(book);
    }

    /**
     * Тестирует валидацию при добавлении книги.
     * В случае нулевого значения хотя бы одного из полей отдает 422 Unprocessable Entity
     * @author Alexandr Filatov
     */
    @Test
    public void testSaveBook_UnprocessableEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/book").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": null, \"author\": \"Test Author\", \"description\": \"Test Description\" }"))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        Mockito.verify(bookService, Mockito.never()).saveBook(ArgumentMatchers.any(Book.class));
    }

    /**
     * Тестирует получение книги по Id.
     * Ожидает возвращение статуса 200 Ok и соответствующей книги
     * @author Alexandr Filatov
     */
    @Test
    public void testGetBook_Success() throws Exception {
        Mockito.when(bookService.getBookById(bookId)).thenReturn(Optional.of(book));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/{bookId}", bookId))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value("Test Author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Description"));

        Mockito.verify(bookService, Mockito.times(1)).getBookById(bookId);
    }

    /**
     * Тестирует получение несуществующей книги по Id.
     * Ожидает возвращение статуса 404 Not Found
     * @author Alexandr Filatov
     */
    @Test
    public void testGetBook_NotFound() throws Exception {
        Mockito.when(bookService.getBookById(bookId)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/{bookId}", bookId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(bookService, Mockito.times(1)).getBookById(bookId);
    }

    /**
     * Тестирует поиск книги по названию.
     * Ожидает статус 200 Ok и соответствующую книгу
     * @author Alexandr Filatov
     */
    @Test
    public void testGetBooksByTitle_Success() throws Exception {
        Mockito.when(bookService.getBooksByTitle(book.getTitle())).thenReturn(List.of(book));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book?title={title}", book.getTitle()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$..title").value("Test Title"));

        Mockito.verify(bookService, Mockito.times(1)).getBooksByTitle(book.getTitle());
    }

    /**
     * Тестирует поиск по названию несуществующей книги.
     * Ожидает возвращение статуса 404 Not Found
     * @author Alexandr Filatov
     */
    @Test
    public void testGetBooksByTitle_NotFound() throws Exception {
        Mockito.when(bookService.getBooksByTitle(book.getTitle())).thenReturn(List.of());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book?title={title}", book.getTitle()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(bookService, Mockito.times(1)).getBooksByTitle(book.getTitle());
    }
}
