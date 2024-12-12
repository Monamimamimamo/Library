package com.urfu.library.service;

import com.urfu.library.model.Reservation;
import com.urfu.library.model.Role;
import com.urfu.library.model.Statistic;
import com.urfu.library.model.User;
import com.urfu.library.model.repository.StatisticRepository;
import com.urfu.library.model.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Класс тестов сервиса для работы со статистикой пользователей
 */
@ExtendWith(MockitoExtension.class)
public class StatisticServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private StatisticRepository statisticRepository;
    @InjectMocks
    private StatisticService statisticService;

    private final User user = new User("alex", "123@gmail.com",
            "qwerty", Role.ROLE_USER);

    private final Statistic statistic = new Statistic(1L, "alex",
            LocalDateTime.now(), 2L, 2L);

    private final Reservation reservation = new Reservation(1L, 1L, true,
            true, LocalDateTime.now().minusHours(1), LocalDateTime.now());

    /**
     * Тест на обновление статистики пользователя при нарушении дедлайна возврата книги
     */
    @Test
    public void updateStatisticTest_LateReturned(){
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(statisticRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(statistic));

        statisticService.updateStatistic(reservation);

        Mockito.verify(statisticRepository, Mockito.times(1))
                .save(ArgumentMatchers.argThat(statistic -> statistic.getLateReturned() == 3L
                        && statistic.getInTimeReturned() == 2L));
    }

    /**
     * Тест на обновление статистики пользователя при возвращении книги вовремя
     */
    @Test
    public void updateStatisticTest_InTimeReturned(){
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(statisticRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(statistic));

        reservation.setDeadlineMissed(false);
        statisticService.updateStatistic(reservation);

        Mockito.verify(statisticRepository, Mockito.times(1))
                .save(ArgumentMatchers.argThat(statistic -> statistic.getInTimeReturned() == 3L
                        && statistic.getLateReturned() == 2L));
    }

    /**
     * Тест на получение статистики по почте пользователя, которого нет в системе
     */
    @Test
    public void getStatisticByEmailTest_UserNotFound() {
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        Optional<Statistic> statisticByEmail = statisticService.getStatisticByEmail(user.getEmail());
        Assertions.assertTrue(statisticByEmail.isEmpty());

        Mockito.verify(statisticRepository, Mockito.never()).findByUsername(ArgumentMatchers.any());
    }

    /**
     * Тест на получение статистики по почте пользователя
     */
    @Test
    public void getStatisticByEmailTest_Success() {
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Optional<Statistic> statisticByEmail = statisticService.getStatisticByEmail(user.getEmail());

        Assertions.assertTrue(statisticByEmail.isEmpty());
        Mockito.verify(statisticRepository, Mockito.times(1))
                .findByUsername(user.getUsername());
    }
}
