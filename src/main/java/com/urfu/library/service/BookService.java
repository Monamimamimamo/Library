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
     * @return Объект Optional, содержащий обновленную книгу, если книга с заданным идентификатором найдена
     * @throws NoSuchElementException если книга отсутствует в системе
     */
    public Optional<Book> updateBookInfo(Long bookId, Book newBookData) {
        Optional<Book> existingBook = bookRepository.findById(bookId);
        if (existingBook.isEmpty()) {
            throw new NoSuchElementException("Book to update not found");
        }
        Book book = existingBook.get();
        book.setTitle(newBookData.getTitle());
        book.setAuthor(newBookData.getAuthor());
        book.setDescription(newBookData.getDescription());

        bookRepository.save(book);
        return Optional.of(book);
    }

    /**
     * Удалить книгу из библиотеки.
     *
     * @param bookId Идентификатор книги, которую нужно удалить.
     * @throws NoSuchElementException если книги нет в системе
     */
    public void deleteBook(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty())
            throw new NoSuchElementException("Book to delete not found");
        bookRepository.deleteById(bookId);
    }

    /**
     * Сохраняет книгу в базу данных
     * @param book книга для сохранения
     * @author Alexandr Filatov
     */
    public void saveBook(Book book) {
        bookRepository.save(book);
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
