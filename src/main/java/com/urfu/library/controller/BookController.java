package com.urfu.library.controller;

import com.urfu.library.model.Book;
import com.urfu.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления операциями с книгами.
 * Обрабатывает HTTP-запросы, связанные с книгами.
 */
@RestController
@RequestMapping("/api/book")
@Validated
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Получает список всех книг.
     * @return ResponseEntity со списком книг и статусом HTTP.
     * HttpStatus: OK, в случае успеха.
     * HttpStatus: NO_CONTENT, в случае отсутствия книг в БД.
     * HttpStatus: UNAUTHORIZED, в случае, если пользователь не авторизовался.
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
     * HttpStatus: UNAUTHORIZED, в случае, если пользователь не авторизовался.
     * HttpStatus: FORBIDDEN, в случае, если пользователь не является Админом.
     */
    @PutMapping("/{bookId}")
    public ResponseEntity<Object> updateBookInfo(@PathVariable("bookId") Long bookId, @RequestBody Book newBook) {
        if (newBook.getTitle() == null || newBook.getAuthor() == null || newBook.getDescription() == null) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        bookService.updateBookInfo(bookId, newBook);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Удаляет книгу по заданному идентификатору книги.
     *
     * @param bookId уникальный идентификатор книги
     * @return ResponseEntity с соответствующим статусом HTTP
     * HttpStatus: OK, в случае успеха.
     * HttpStatus: NO_CONTENT, в случае отсутствия искомой книги в БД.
     * HttpStatus: UNAUTHORIZED, в случае, если пользователь не авторизовался.
     * HttpStatus: FORBIDDEN, в случае, если пользователь не является Админом.
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Создает новую книгу, добавляет ее в каталог
     * @param book книга для добавления в каталог
     * @return HTTP status:
     * <ul>
     *     <li>201 Created</li>
     *     <li>422 Unprocessable Entity</li>
     *     <li>401 UNAUTHORIZED</li>
     *     <li>403 Forbidden</li>
     * </ul>
     * @author Alexandr Filatov
     */
    @PostMapping
    public ResponseEntity<Object> createBook(@Valid @RequestBody Book book) {
        bookService.saveBook(book);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Возвращает книгу по id
     * @param bookId идентификатор книги
     * @return HTTP status:
     * <ul>
     *     <li>200 Success</li>
     *     <li>404 Not Found</li>
     *     <li>401 UNAUTHORIZED</li>
     * </ul>
     * @author Alexandr Filatov
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable("bookId") Long bookId) {
        Optional<Book> book = bookService.getBookById(bookId);
        if (book.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(book.get(), HttpStatus.OK);
    }

    /**
     * Возвращает список книг соответствующих запрашиваемому названию
     * @param title название книги
     * @return HTTP status:
     * <ul>
     *     <li>200 Success</li>
     *     <li>404 Not Found</li>
     *     <li>401 UNAUTHORIZED</li>
     * </ul>
     * @author Alexandr Filatov
     */
    @GetMapping
    public ResponseEntity<List<Book>> getBooksByTitle(@RequestParam String title) {
        List<Book> books = bookService.getBooksByTitle(title);
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}
