package com.urfu.library.service;

import com.urfu.library.model.Book;
import com.urfu.library.model.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Класс реализует модульные тесты для сервиса книг
 */
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private Integer bookId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookId = 1;
        book = new Book();
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setDescription("Test Description");
    }

    /**
     * Тест для проверки успешного получения всех книг.
     * Данный тест симулирует наличие двух книг в БД, и проверяет,
     * что метод репозитория findAll() вызывается.
     */
    @Test
    void testGetAllBooks() {
        List<Book> books = Arrays.asList(book, book);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    /**
     * Данный тест симулирует отсутствие книг в БД, и проверяет,
     * что метод репозитория findAll() вызывается.
     */
    @Test
    void testGetAllBooks_NoBooks() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<Book> result = bookService.getAllBooks();

        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    /**
     * Тест для проверки успешного обновления информации о книге.
     * Данный тест симулирует наличие книги в БД, и проверяет, что информация
     * о книге успешно обновляется, а метод репозитория save() вызывается.
     */
    @Test
    public void testUpdateBookInfo_Success() {
        Book newBookData = new Book();
        newBookData.setTitle("Updated Title");
        newBookData.setAuthor("Updated Author");
        newBookData.setDescription("Updated Description");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Optional<Book> updatedBook = bookService.updateBookInfo(bookId, newBookData);

        assertTrue(updatedBook.isPresent());
        assertEquals("Updated Title", updatedBook.get().getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    /**
     * Тест для проверки ситуации, когда обновление книги невозможно,
     * так как книга с указанным ID не найдена.
     * Тест проверяет, что метод репозитория save() не вызывается,
     * а возвращаемое значение пустое.
     */
    @Test
    public void testUpdateBookInfo_BookNotFound() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        Optional<Book> updatedBook = bookService.updateBookInfo(bookId, book);

        assertTrue(updatedBook.isEmpty());
        verify(bookRepository, never()).save(any(Book.class));
    }

    /**
     * Тест для проверки успешного удаления книги из БД.
     * Тест симулирует наличие книги в БД и проверяет, что
     * книга успешно удаляется, а метод репозитория deleteById() вызывается.
     */
    @Test
    public void testDeleteBook_Success() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        boolean result = bookService.deleteBook(bookId);

        assertTrue(result);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    /**
     * Тест для проверки ситуации, когда удаление книги невозможно,
     * так как книга с указанным ID не найдена в БД.
     * Тест проверяет, что метод репозитория deleteById() не вызывается.
     */
    @Test
    public void testDeleteBook_BookNotFound() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        boolean result = bookService.deleteBook(bookId);

        assertFalse(result);
        verify(bookRepository, never()).deleteById(any(Integer.class));
    }

    /**
     * Тестирует добавление новой книги
     * @author Alexandr Filatov
     */
    @Test
    public void testCreateBook_Success() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookService.saveBook(book);
        Optional<Book> savedBook = bookRepository.findById(bookId);
        assertTrue(savedBook.isPresent());
        assertEquals(book, savedBook.get());
        verify(bookRepository, times(1)).save(book);
    }

    /**
     * Проверка на создание некорректного экземпляра книги,
     * в бд ничего сохранено не будет
     * @author Alexandr Filatov
     */
    @Test
    public void testCreateBook_UnprocessableEntity() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Book unprocessableBook = new Book();
        unprocessableBook.setTitle("");
        unprocessableBook.setAuthor("Author");
        unprocessableBook.setDescription("Description");
        bookService.saveBook(book);
        Optional<Book> savedBook = bookRepository.findById(unprocessableBook.getId());
        assertTrue(savedBook.isEmpty());
        verify(bookRepository, times(1)).save(book);
    }

    /**
     * Тестирует получение книги по Id
     * @author Alexandr Filatov
     */
    @Test
    public void testGetBookById_Success() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookById(bookId);

        assertEquals(book, foundBook);
        verify(bookRepository, times(1)).findById(bookId);
    }

    /**
     * Тестирует выбрасывание ошибки NoSuchElementException
     * в случае отсутствия книги с заданным ID в бд
     * @author Alexandr Filatov
     */
    @Test
    public void testGetBookById_BookNotFound() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> bookService.getBookById(2));
    }

    /**
     * Тестирует поиск книги по названию
     * @author Alexandr Filatov
     */
    @Test
    public void testFindBookByTitle_Success() {
        when(bookRepository.findByTitle(book.getTitle())).thenReturn(List.of(book));
        List<Book> foundBook = bookService.getBooksByTitle(book.getTitle());
        assertFalse(foundBook.isEmpty());
        assertEquals(foundBook.getFirst(), book);
        verify(bookRepository, times(1)).findByTitle(book.getTitle());
    }

    /**
     * Тестирует выбрасывание ошибки NoSuchElementException
     * в случае отсутствия книги с заданным названием в бд
     * @author Alexandr Filatov
     */
    @Test
    public void testFindBookByTitle_BookNotFound() {
        when(bookRepository.findByTitle("NotFoundTitle")).thenReturn(List.of());

        Assertions.assertThrows(NoSuchElementException.class, () -> bookService.getBooksByTitle("NotFoundTitle"));
    }
}
