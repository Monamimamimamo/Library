package com.urfu.library.model.repository;

import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий сущности User для взаимодействия с базой данных
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Поиск пользователя по логину
     */
    Optional<User> findByUsername(String username);

    /**
     * Поиск пользователя по email
     */
    Optional<User> findByEmail(String email);

    /**
     * Поиск всех пользователей с конкретной ролью
     */
    List<User> findAllByRole(Role role);
}
