package com.urfu.library.controller;

import com.urfu.library.model.Book;
import com.urfu.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Object> updateBookInfo(@PathVariable("bookId") UUID bookId, @RequestBody Book newBook) {
        if (newBook.getTitle() == null || newBook.getAuthor() == null || newBook.getDescription() == null)
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);

        Optional<Book> updatedBook = bookService.updateBookInfo(bookId, newBook);
        if (updatedBook.isPresent())
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable("bookId") UUID bookId) {
        boolean deleted = bookService.deleteBook(bookId);
        if (deleted)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
