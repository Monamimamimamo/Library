package com.urfu.library.controller;

import com.urfu.library.model.Book;
import com.urfu.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Контроллер для управления операциями с книгами.
 * Обрабатывает HTTP-запросы, связанные с книгами.
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * Получает список всех книг.
     * @return ResponseEntity с списком книг и статусом HTTP.
     * HttpStatus: OK, в случае успеха.
     * HttpStatus: NO_CONTENT, в случае отсутствия книг в БД.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    /**
     * Обновляет информацию о книге по заданному идентификатору книги.
     *
     * @param bookId   уникальный идентификатор книги
     * @param newBook  новый объект книги с обновленной информацией
     * @return ResponseEntity с соответствующим статусом HTTP
     * HttpStatus: OK, в случае успеха.
     * HttpStatus: NO_CONTENT, в случае отсутствия искомой книги в БД.
     * HttpStatus: UNPROCESSABLE_ENTITY, в случае некорректности входных данных для книги.
     */
    @PutMapping("/{bookId}")
    public ResponseEntity<Object> updateBookInfo(@PathVariable("bookId") UUID bookId, @RequestBody Book newBook) {
        if (newBook.getTitle() == null || newBook.getAuthor() == null || newBook.getDescription() == null)
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);

        Optional<Book> updatedBook = bookService.updateBookInfo(bookId, newBook);
        if (updatedBook.isPresent())
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Удаляет книгу по заданному идентификатору книги.
     *
     * @param bookId уникальный идентификатор книги
     * @return ResponseEntity с соответствующим статусом HTTP
     * HttpStatus: OK, в случае успеха.
     * HttpStatus: NO_CONTENT, в случае отсутствия искомой книги в БД.Ъ
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable("bookId") UUID bookId) {
        boolean deleted = bookService.deleteBook(bookId);
        if (deleted)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
