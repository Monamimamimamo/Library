package com.urfu.library.service;

import com.urfu.library.model.Reservation;
import com.urfu.library.model.Statistic;
import com.urfu.library.model.User;
import com.urfu.library.model.repository.StatisticRepository;
import com.urfu.library.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для работы со статистикой пользователя
 */
@Service
public class StatisticService {
    private final StatisticRepository statisticRepository;
    private final UserRepository userRepository;

    @Autowired
    public StatisticService(StatisticRepository statisticRepository, UserRepository userRepository) {
        this.statisticRepository = statisticRepository;
        this.userRepository = userRepository;
    }

    /**
     * Получение статистики по имени пользователя
     */
    public Optional<Statistic> getStatisticByUsername(String username) {
        return statisticRepository.findByUsername(username);
    }

    /**
     * Получение статистики по почте пользователя
     */
    public Optional<Statistic> getStatisticByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        return statisticRepository.findByUsername(user.get().getUsername());
    }

    /**
     * Обновляет статистику пользователя в зависимости от статуса нарушения дедлайна
     */
    public void updateStatistic(Reservation reservation) {
        User user = userRepository.findById(reservation.getUserId()).get();
        Statistic statistic = statisticRepository.findByUsername(user.getUsername()).get();
        if (reservation.isDeadlineMissed()) {
            statistic.setLateReturned(statistic.getLateReturned() + 1);
        } else {
            statistic.setInTimeReturned(statistic.getInTimeReturned() + 1);
        }
        statisticRepository.save(statistic);
    }
}
