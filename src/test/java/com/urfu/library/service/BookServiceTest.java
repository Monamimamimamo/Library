package com.urfu.library.service;

import com.urfu.library.model.Book;
import com.urfu.library.model.BookRepo;
import com.urfu.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
     * что  метод репозитория findAll() вызывается.
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
     * что  метод репозитория findAll() вызывается.
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
}
