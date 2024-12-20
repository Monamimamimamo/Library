package com.urfu.library.model.repository;

import com.urfu.library.model.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий сущности Statistic для работы с базой данных
 */
@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    /**
     * Возвращает статистику заданного пользователя
     */
    Optional<Statistic> findByUsername(String username);
}
