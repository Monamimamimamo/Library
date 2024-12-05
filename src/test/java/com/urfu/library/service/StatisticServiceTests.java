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

    private User user = new User("alex", "123@gmail.com","qwerty", Role.ROLE_USER);
    private Statistic statistic = new Statistic(1L, "alex",
            LocalDateTime.now(), 2L, 2L);
    private Reservation reservation = new Reservation(1L, 1L, true, true,
            LocalDateTime.now().minusHours(1), LocalDateTime.now());


    /**
     * Тест на получение статистики по пользователю, которого нет в системе
     */
    @Test
    public void getStatisticByUsernameTest_NotFound() {
        Mockito.when(statisticRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        Optional<Statistic> statisticByUsername = statisticService.getStatisticByUsername(user.getUsername());
        Assertions.assertTrue(statisticByUsername.isEmpty());

        Mockito.verify(statisticRepository, Mockito.times(1)).findByUsername(user.getUsername());
    }

    /**
     * Тест на получение статистики по пользователю
     */
    @Test
    public void getStatisticByUsernameTest_Success() {
        Mockito.when(statisticRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(statistic));
        Optional<Statistic> statisticByUsername = statisticService.getStatisticByUsername(user.getUsername());
        Assertions.assertTrue(statisticByUsername.isPresent());
        Assertions.assertEquals(statistic, statisticByUsername.get());

        Mockito.verify(statisticRepository, Mockito.times(1))
                .findByUsername(user.getUsername());
    }

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
        Mockito.verify(statisticRepository, Mockito.times(1)).save(ArgumentMatchers
                .argThat(statistic -> statistic.getLateReturned() == 3L && statistic.getInTimeReturned() == 2L));
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
        Mockito.verify(statisticRepository, Mockito.times(1)).save(ArgumentMatchers
                .argThat(statistic -> statistic.getInTimeReturned() == 3L && statistic.getLateReturned() == 2L));
    }
}
