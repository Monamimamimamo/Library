package com.urfu.library.service;

import com.urfu.library.model.Book;
import com.urfu.library.model.BookRepo;
import com.urfu.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @Test
    public void testUpdateBookInfo_BookNotFound() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.empty());
        Optional<Book> updatedBook = bookService.updateBookInfo(bookId, book);

        assertTrue(updatedBook.isEmpty());
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    public void testDeleteBook_Success() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        boolean result = bookService.deleteBook(bookId);

        assertTrue(result);
        verify(bookRepo, times(1)).deleteById(bookId);
    }

    @Test
    public void testDeleteBook_BookNotFound() {
        when(bookRepo.findById(bookId)).thenReturn(Optional.empty());
        boolean result = bookService.deleteBook(bookId);

        assertFalse(result);
        verify(bookRepo, never()).deleteById(any(UUID.class));
    }
}
