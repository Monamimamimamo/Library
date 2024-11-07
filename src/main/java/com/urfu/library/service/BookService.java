package com.urfu.library.service;

import com.urfu.library.model.Book;
import com.urfu.library.model.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Сервис для управления книгами в библиотеке.
 * Предоставляет методы для получения, обновления и удаления книг.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Получить список всех книг в библиотеке.
     * Или пустой список, в случае отсутствия книг.
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Обновить информацию о книге.
     *
     * @param bookId      Идентификатор книги, которую нужно обновить.
     * @param newBookData Новый объект книги с обновленной информацией.
     * @return Объект Optional, содержащий обновленную книгу, если книга с заданным идентификатором найдена, иначе пустой Optional.
     */
    public Optional<Book> updateBookInfo(Long bookId, Book newBookData) {
        Optional<Book> existingBook = bookRepository.findById(bookId);
        if (existingBook.isPresent()) {
            Book book = existingBook.get();
            book.setTitle(newBookData.getTitle());
            book.setAuthor(newBookData.getAuthor());
            book.setDescription(newBookData.getDescription());

            bookRepository.save(book);
            return Optional.of(book);
        }
        return Optional.empty();
    }

    /**
     * Удалить книгу из библиотеки.
     *
     * @param bookId Идентификатор книги, которую нужно удалить.
     * @return true, если книга была успешно удалена; false, если книга с заданным идентификатором не найдена.
     */
    public boolean deleteBook(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty())
            return false;
        bookRepository.deleteById(bookId);
        return true;
    }

    /**
     * Сохраняет книгу в базу данных
     * @param newBook книга для сохранения
     * @author Alexandr Filatov
     */
    public void saveBook(Book newBook) {
        bookRepository.save(newBook);
    }

    /**
     * Возвращает книгу по ID
     * @param bookId ID книги
     * @return найденную книгу
     * @author Alexandr Filatov
     */
    public Optional<Book> getBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }

    /**
     * Возвращает список книг по названию
     * @param title название книги
     * @return найденные книги
     */
    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitle(title);
    }
}
