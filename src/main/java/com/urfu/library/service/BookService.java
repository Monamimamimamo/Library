package com.urfu.library.service;

import com.urfu.library.model.Book;
import com.urfu.library.model.BookRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для управления книгами в библиотеке.
 * Предоставляет методы для получения, обновления и удаления книг.
 */
@Service
public class BookService {

    private final BookRepo bookRepo;

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    /**
     * Получить список всех книг в библиотеке.
     * Или пустой список, в случае отсутствия книг.
     */
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    /**
     * Обновить информацию о книге.
     *
     * @param bookId      Идентификатор книги, которую нужно обновить.
     * @param newBookData Новый объект книги с обновленной информацией.
     * @return Объект Optional, содержащий обновленную книгу, если книга с заданным идентификатором найдена, иначе пустой Optional.
     */
    public Optional<Book> updateBookInfo(UUID bookId, Book newBookData) {
        Optional<Book> existingBook = bookRepo.findById(bookId);
        if (existingBook.isPresent()) {
            Book book = existingBook.get();
            book.setTitle(newBookData.getTitle());
            book.setAuthor(newBookData.getAuthor());
            book.setDescription(newBookData.getDescription());

            bookRepo.save(book);
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
    public boolean deleteBook(UUID bookId) {
        Optional<Book> book = bookRepo.findById(bookId);
        if (book.isEmpty())
            return false;
        bookRepo.deleteById(bookId);
        return true;
    }
}
