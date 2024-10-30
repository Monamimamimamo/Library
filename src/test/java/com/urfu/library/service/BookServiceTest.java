package com.urfu.library.service;

import com.urfu.library.model.Book;
import com.urfu.library.model.BookRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private UUID bookId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookId = UUID.randomUUID();
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
        when(bookRepo.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(bookRepo, times(1)).findAll();
    }

    /**
     * Данный тест симулирует отсутствие книг в БД, и проверяет,
     * что метод репозитория findAll() вызывается.
     */
    @Test
    void testGetAllBooks_NoBooks() {
        when(bookRepo.findAll()).thenReturn(List.of());

        List<Book> result = bookService.getAllBooks();

        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(bookRepo, times(1)).findAll();
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

        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        Optional<Book> updatedBook = bookService.updateBookInfo(bookId, newBookData);

        assertTrue(updatedBook.isPresent());
        assertEquals("Updated Title", updatedBook.get().getTitle());
        verify(bookRepo, times(1)).save(any(Book.class));
    }

    /**
     * Тест для проверки ситуации, когда обновление книги невозможно,
     * так как книга с указанным ID не найдена.
     * Тест проверяет, что метод репозитория save() не вызывается,
     * а возвращаемое значение пустое.
     */
    @Test
    public void testUpdateBookInfo_BookNotFound() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.empty());
        Optional<Book> updatedBook = bookService.updateBookInfo(bookId, book);

        assertTrue(updatedBook.isEmpty());
        verify(bookRepo, never()).save(any(Book.class));
    }

    /**
     * Тест для проверки успешного удаления книги из БД.
     * Тест симулирует наличие книги в БД и проверяет, что
     * книга успешно удаляется, а метод репозитория deleteById() вызывается.
     */
    @Test
    public void testDeleteBook_Success() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        boolean result = bookService.deleteBook(bookId);

        assertTrue(result);
        verify(bookRepo, times(1)).deleteById(bookId);
    }

    /**
     * Тест для проверки ситуации, когда удаление книги невозможно,
     * так как книга с указанным ID не найдена в БД.
     * Тест проверяет, что метод репозитория deleteById() не вызывается.
     */
    @Test
    public void testDeleteBook_BookNotFound() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.empty());
        boolean result = bookService.deleteBook(bookId);

        assertFalse(result);
        verify(bookRepo, never()).deleteById(any(UUID.class));
    }

    /**
     * Тестирует добавление новой книги
     */
    @Test
    public void testCreateBook_Success() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));

        bookService.saveBook(book);
        Optional<Book> savedBook = bookRepo.findById(bookId);
        assertTrue(savedBook.isPresent());
        assertEquals(book, savedBook.get());
        verify(bookRepo, times(1)).save(book);
    }

    /**
     * Проверка на создание некорректного экземпляра книги,
     * в бд ничего сохранено не будет
     */
    @Test
    public void testCreateBook_UnprocessableEntity() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        Book unprocessableBook = new Book();
        unprocessableBook.setTitle("");
        unprocessableBook.setAuthor("Author");
        unprocessableBook.setDescription("Description");
        bookService.saveBook(book);
        Optional<Book> savedBook = bookRepo.findById(unprocessableBook.getId());
        assertTrue(savedBook.isEmpty());
        verify(bookRepo, times(1)).save(book);
    }

    /**
     * Тестирует получение книги по Id
     */
    @Test
    public void testGetBookById_Success() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getBookById(bookId);

        assertTrue(foundBook.isPresent());
        assertEquals(book, foundBook.get());
        verify(bookRepo, times(1)).findById(bookId);
    }

    /**
     * Проверка на возвращение Optional.empty() в случае отсутствия книги в бд
     */
    @Test
    public void testGetBookById_BookNotFound() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.empty());
        Optional<Book> foundBook = bookService.getBookById(bookId);

        assertTrue(foundBook.isEmpty());
    }

    /**
     * Тестирует поиск книги по названию
     */
    @Test
    public void testFindBookByTitle_Success() {
        when(bookRepo.findByTitle(book.getTitle())).thenReturn(List.of(book));
        List<Book> foundBook = bookService.getBooksByTitle(book.getTitle());
        assertFalse(foundBook.isEmpty());
        assertEquals(foundBook.getFirst(), book);
        verify(bookRepo, times(1)).findByTitle(book.getTitle());
    }

    /**
     * Тестирует выбрасывание ошибки NoSuchElementException
     * в случае отсутствия книги с заданным названием в бд
     */
    @Test
    public void testFindBookByTitle_BookNotFound() {
        when(bookRepo.findByTitle("NotFoundTitle")).thenReturn(List.of());

        Assertions.assertThrows(NoSuchElementException.class, () -> bookService.getBooksByTitle("NotFoundTitle"));
    }
}
