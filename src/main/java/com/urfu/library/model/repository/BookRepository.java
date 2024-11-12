package com.urfu.library.model.repository;

import com.urfu.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий сущности Book для взаимодействия с базой данных
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    /**
     * Поиск книг по заданному заголовку
     */
    List<Book> findByTitle(String title);
}
