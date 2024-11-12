package com.urfu.library.model.repository;

import com.urfu.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий сущности User для взаимодействия с базой данных
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Поиск пользователя по логину
     */
    User findByUsername(String username);
}