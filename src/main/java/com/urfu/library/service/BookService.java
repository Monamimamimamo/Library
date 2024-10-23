package com.urfu.library.service;

import com.urfu.library.model.Book;
import com.urfu.library.model.BookRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepo bookRepo;

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }


    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

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

    public boolean deleteBook(UUID bookId) {
        Optional<Book> book = bookRepo.findById(bookId);
        if (book.isEmpty())
            return false;
        bookRepo.deleteById(bookId);
        return true;
    }
}
