package com.urfu.library.service;

import com.urfu.library.model.BookRepo;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepo bookRepo;

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }


}
