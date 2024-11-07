package com.urfu.library.service;

import com.urfu.library.model.Book;
import com.urfu.library.model.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

/**
 * Класс реализует модульные тесты для сервиса книг
 */
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private Long bookId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookId = 1L;
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
        Mockito.when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(2, result.size());
        Mockito.verify(bookRepository, Mockito.times(1)).findAll();
    }

    /**
     * Данный тест симулирует отсутствие книг в БД, и проверяет,
     * что метод репозитория findAll() вызывается.
     */
    @Test
    void testGetAllBooks_NoBooks() {
        Mockito.when(bookRepository.findAll()).thenReturn(List.of());

        List<Book> result = bookService.getAllBooks();

        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.size());
        Mockito.verify(bookRepository, Mockito.times(1)).findAll();
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

        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Optional<Book> updatedBook = bookService.updateBookInfo(bookId, newBookData);

        Assertions.assertTrue(updatedBook.isPresent());
        Assertions.assertEquals("Updated Title", updatedBook.get().getTitle());
        Mockito.verify(bookRepository, Mockito.times(1)).save(ArgumentMatchers.any(Book.class));
    }

    /**
     * Тест для проверки ситуации, когда обновление книги невозможно,
     * так как книга с указанным ID не найдена.
     * Тест проверяет, что метод репозитория save() не вызывается,
     * а возвращаемое значение пустое.
     */
    @Test
    public void testUpdateBookInfo_BookNotFound() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        Optional<Book> updatedBook = bookService.updateBookInfo(bookId, book);

        Assertions.assertTrue(updatedBook.isEmpty());
        Mockito.verify(bookRepository, Mockito.never()).save(ArgumentMatchers.any(Book.class));
    }

    /**
     * Тест для проверки успешного удаления книги из БД.
     * Тест симулирует наличие книги в БД и проверяет, что
     * книга успешно удаляется, а метод репозитория deleteById() вызывается.
     */
    @Test
    public void testDeleteBook_Success() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        boolean result = bookService.deleteBook(bookId);

        Assertions.assertTrue(result);
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(bookId);
    }

    /**
     * Тест для проверки ситуации, когда удаление книги невозможно,
     * так как книга с указанным ID не найдена в БД.
     * Тест проверяет, что метод репозитория deleteById() не вызывается.
     */
    @Test
    public void testDeleteBook_BookNotFound() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        boolean result = bookService.deleteBook(bookId);

        Assertions.assertFalse(result);
        Mockito.verify(bookRepository, Mockito.never()).deleteById(ArgumentMatchers.any(Long.class));
    }

    /**
     * Тестирует добавление новой книги
     * @author Alexandr Filatov
     */
    @Test
    public void testCreateBook_Success() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookService.saveBook(book);
        Optional<Book> savedBook = bookRepository.findById(bookId);

        Assertions.assertTrue(savedBook.isPresent());
        Assertions.assertEquals(book, savedBook.get());
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    /**
     * Тестирует успешное получение книги по Id
     * @author Alexandr Filatov
     */
    @Test
    public void testGetBookById_Success() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getBookById(bookId);

        Assertions.assertTrue(foundBook.isPresent());
        Assertions.assertEquals(book, foundBook.get());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(bookId);
    }

    /**
     * Тестирует получение Optional.empty()
     * в случае отсутствия книги с заданным ID в бд
     * @author Alexandr Filatov
     */
    @Test
    public void testGetBookById_BookNotFound() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Optional<Book> foundBook = bookService.getBookById(bookId);
        Assertions.assertTrue(foundBook.isEmpty());
    }

    /**
     * Тестирует успешный поиск книги по названию
     * @author Alexandr Filatov
     */
    @Test
    public void testFindBookByTitle_Success() {
        Mockito.when(bookRepository.findByTitle(book.getTitle())).thenReturn(List.of(book));
        List<Book> foundBook = bookService.getBooksByTitle(book.getTitle());
        Assertions.assertFalse(foundBook.isEmpty());
        Assertions.assertEquals(foundBook.getFirst(), book);
        Mockito.verify(bookRepository, Mockito.times(1)).findByTitle(book.getTitle());
    }

    /**
     * Тестирует получение пустого списка
     * в случае отсутствия книги с заданным названием в бд
     * @author Alexandr Filatov
     */
    @Test
    public void testFindBookByTitle_BookNotFound() {
        Mockito.when(bookRepository.findByTitle("NotFoundTitle")).thenReturn(List.of());

        List<Book> foundBook = bookService.getBooksByTitle("NotFoundTitle");
        Assertions.assertTrue(foundBook.isEmpty());
    }
}
